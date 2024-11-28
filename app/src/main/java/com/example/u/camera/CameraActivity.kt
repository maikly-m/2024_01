package com.example.u.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.example.u.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class CameraActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var takePictureButton: Button

    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private lateinit var handler: Handler
    private lateinit var backgroundHandler: Handler
    private var cameraId: String = ""

    // 权限请求相关
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 设置全屏
        setFullScreen(window)

        setContentView(R.layout.activity_camera)

        textureView = findViewById(R.id.textureView)
        takePictureButton = findViewById(R.id.takePictureButton)

        handler = Handler(Looper.getMainLooper())

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // 请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        takePictureButton.setOnClickListener {
            takePicture()
        }
    }

    private fun setFullScreen(window: Window?): Pair<Int, Int>? {
        if (window == null) return null
        val attributes = window.attributes
        var oldCutoutMode = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            oldCutoutMode = attributes.layoutInDisplayCutoutMode
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        val oldOption = window.decorView.systemUiVisibility
        val option = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = option
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
            if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                window.decorView.systemUiVisibility = option
            }
        }
        return Pair(oldCutoutMode, oldOption)
    }

    fun getScreenSize(): Size {
        val size: Size
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowMetrics = windowManager.currentWindowMetrics
            size = Size(windowMetrics.bounds.width(), windowMetrics.bounds.height())
        } else {
            val display = windowManager.defaultDisplay
            val point = Point()
            display.getRealSize(point)
            size = Size(point.x, point.y)
        }
        return size
    }

    private fun startCamera() {
        Timber.d("startCamera")
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                Timber.d("onSurfaceTextureAvailable")
                // 获取默认相机 ID
                try {
                    cameraId = cameraManager.cameraIdList[0] // 使用后置摄像头（ID 0）

                    // 获取相机特性
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    // 获取支持的预览尺寸
                    val outputSizes = map?.getOutputSizes(SurfaceTexture::class.java)
                    val bestSize = chooseOptimalSize(outputSizes)

                    // 获取相机支持的输出格式和尺寸
                    val size = choosePictureSize(outputSizes)

                    Timber.d("bestSize = ${bestSize.toString()}")
                    Timber.d("size = ${size.toString()}")
                    imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)

                    // 打开相机
                    if (ActivityCompat.checkSelfPermission(
                            this@CameraActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }

                    // 配置输出尺寸为最佳尺寸
                    val previewSize = bestSize
                    surface.setDefaultBufferSize(previewSize.width, previewSize.height)
                    val s = Surface(surface)
                    cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                        override fun onOpened(camera: CameraDevice) {
                            cameraDevice = camera
                            createCameraPreviewSession(camera, s, bestSize)
                        }

                        override fun onDisconnected(camera: CameraDevice) {
                            cameraDevice?.close()
                        }

                        override fun onError(camera: CameraDevice, error: Int) {
                            Timber.e( "Camera error: $error")
                        }
                    }, handler)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    private fun chooseOptimalSize(outputSizes: Array<Size>?): Size {
        val screenSize = getScreenSize()

        Timber.d("chooseOptimalSize widthPixels=${screenSize.width.toFloat()}")
        Timber.d("chooseOptimalSize heightPixels=${screenSize.height.toFloat()}")
        val screenAspectRatio = screenSize.width.toFloat() / screenSize.height.toFloat()
        Timber.d("chooseOptimalSize screenAspectRatio=${screenAspectRatio}")

        var bestSize: Size = outputSizes?.get(0) ?: Size(1920, 1080)

        // 获取精确值
        outputSizes?.forEach { size ->
            if (size.width == screenSize.width && size.height == screenSize.height){
                return size
            }
            if (size.width == screenSize.height && size.height == screenSize.width){
                return size
            }
        }
        // 获取接近值
        val map = hashMapOf<Float, Size>()
        outputSizes?.forEach { size ->
            val aspectRatio: Float = if (screenAspectRatio > 1){
                size.width.toFloat() / size.height.toFloat()
            }else{
                size.height.toFloat() / size.width.toFloat()
            }
            val ratioDiff = abs(aspectRatio - screenAspectRatio)
            if (map[ratioDiff] != null) {
                map[ratioDiff]?.width?.also {
                    if (it < size.width){
                        // 使用分辨率高的
                        map[ratioDiff] = size
                    }
                }
            } else {
                map[ratioDiff] = size
            }
            Timber.d("chooseOptimalSize size=$size")
        }
        val min = map.keys.min()
        Timber.d("chooseOptimalSize min=$min")
        return map[min] ?: bestSize
    }
    private fun choosePictureSize(outputSizes: Array<Size>?): Size {

        var bestSize: Size = outputSizes?.get(0) ?: Size(1920, 1080)

        // 获取拍照值
        outputSizes?.forEach { size ->
            if (size.width == 1080 && size.height == 1920){
                return size
            }
            if (size.width == 1920 && size.height == 1080){
                return size
            }
        }
        outputSizes?.forEach { size ->
            if (size.width == 1280 && size.height == 720){
                return size
            }
            if (size.width == 720 && size.height == 1280){
                return size
            }
        }
        return bestSize
    }

    private fun createCameraPreviewSession(surface: Surface) {
        try {
            val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)

            // 创建 CameraCaptureSession，用于相机预览
            cameraDevice?.createCaptureSession(
                listOf(surface, imageReader?.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        val previewRequest = captureRequestBuilder?.build()
                        captureSession?.setRepeatingRequest(previewRequest!!, null, backgroundHandler)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(this@CameraActivity, "Failed to configure camera", Toast.LENGTH_SHORT).show()
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreviewSession(camera: CameraDevice, surface: Surface, bestSize: Size) {
        try {
            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            val previewRequest = captureRequestBuilder.build()

            camera.createCaptureSession(
                listOf(surface, imageReader?.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        captureSession?.setRepeatingRequest(previewRequest, null, backgroundHandler)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(this@CameraActivity, "Failed to configure camera", Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
            backgroundHandler
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        try {
            // 创建拍照请求
            val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            imageReader?.surface?.let { captureRequestBuilder?.addTarget(it) }

            // 设置拍照方向
            captureRequestBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getOrientation())

            // 拍照并保存
            val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    Timber.d("onCaptureCompleted")
                    saveImage(result)
                }
            }

            captureSession?.capture(captureRequestBuilder?.build()!!, captureCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun saveImage(result: TotalCaptureResult) {
        // 这里保存照片
        val image = imageReader?.acquireLatestImage()
        if (image != null) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            val file = File(getExternalFilesDir(null), "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg")
            try {
                val outputStream = FileOutputStream(file)
                outputStream.write(bytes)
                outputStream.close()
                image.close()
                textureView.post {
                    Toast.makeText(this, "Photo saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getOrientation(): Int {
        // 获取设备的旋转角度
        val rotation = windowManager.defaultDisplay.rotation
        return when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundHandler = Handler(Looper.myLooper()!!)
    }

    override fun onPause() {
        super.onPause()
        cameraDevice?.close()
    }
}
