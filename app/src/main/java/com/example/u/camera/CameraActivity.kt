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
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.u.R
import com.example.u.camera.preview.PreSize
import com.example.u.ui.dashboard.DashboardViewModel
import com.example.u.uitls.setFullScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

class CameraActivity : AppCompatActivity() {

    private lateinit var viewModel: CameraViewModel
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
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
        viewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        textureView = findViewById(R.id.textureView)
        takePictureButton = findViewById(R.id.takePictureButton)

        handler = Handler(Looper.getMainLooper())

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // 请求权限
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        takePictureButton.setOnClickListener {
            takePicture()
        }
    }

    private fun getScreenSize(): Size {
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
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                // 获取默认相机 ID
                try {
                    cameraId = getBackCameraId() ?: cameraManager.cameraIdList[0] // 使用后置摄像头（ID 0）
                    // cameraId = getFrontCameraId() ?: cameraManager.cameraIdList[0] // 使用后置摄像头（ID 0）

                    // 获取相机特性
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    // 获取支持的预览尺寸
                    val outputSizes = map?.getOutputSizes(SurfaceTexture::class.java)
                    val bestSize = chooseOptimalSize(outputSizes)

                    // 获取相机支持的输出格式和尺寸
                    val size = choosePictureSize(outputSizes)

                    Timber.d("bestSize = ${bestSize.toString()}")
                    Timber.d("size = ${size.toString()}")
                    imageReader =
                        ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)
                    // 创建拍照回调
                    imageReader!!.setOnImageAvailableListener({ reader ->
                        saveImage(null)
                    }, backgroundHandler)

                    // 打开相机
                    if (ActivityCompat.checkSelfPermission(
                            this@CameraActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
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
                            Timber.e("Camera error: $error")
                        }
                    }, handler)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }
    // 获取前置摄像头ID
    private fun getFrontCameraId(): String? {
        try {
            val cameraList = cameraManager.cameraIdList
            for (cameraId in cameraList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }
    // 获取后置摄像头ID
    private fun getBackCameraId(): String? {
        try {
            val cameraList = cameraManager.cameraIdList
            for (cameraId in cameraList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    // 获取当前设备的旋转角度
    private fun getRotationDegrees(rotation: Int): Int {
        return when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    private fun chooseOptimalSize(outputSizes: Array<Size>?): Size {
        val screenSizeTemp = getScreenSize()
        var screenSize = screenSizeTemp
        // 镜头的宽高和屏幕要翻转
        screenSize = Size(screenSizeTemp.height, screenSizeTemp.width)
        val bestSize: Size = outputSizes?.get(0) ?: Size(1920, 1080)
        outputSizes?.let {
            val l = List(it.size) { index ->
                PreSize(it[index].width, it[index].height)
            }
            getBestPreviewOrder(l,  PreSize(screenSize.width, screenSize.height), true)[0].run {
                val size = Size(width, height)
                Timber.d("chooseOptimalSize 0 screenSize=${Size(screenSize.width, screenSize.height)}")
                Timber.d("chooseOptimalSize 0 bestPreviewOrder=$size")
                return size
            }
        }
        Timber.d("chooseOptimalSize 1 bestPreviewOrder=$bestSize")
        return  bestSize
    }

    private fun chooseOptimalSizeTest(outputSizes: Array<Size>?): Size {
        val screenSize = getScreenSize()

        Timber.d("chooseOptimalSize widthPixels=${screenSize.width.toFloat()}")
        Timber.d("chooseOptimalSize heightPixels=${screenSize.height.toFloat()}")
        val screenAspectRatio = screenSize.width.toFloat() / screenSize.height.toFloat()
        Timber.d("chooseOptimalSize screenAspectRatio=${screenAspectRatio}")

        var bestSize: Size = outputSizes?.get(0) ?: Size(1920, 1080)

        // 获取精确值
        outputSizes?.forEach { size ->
            if (size.width == screenSize.width && size.height == screenSize.height) {
                return size
            }
            if (size.width == screenSize.height && size.height == screenSize.width) {
                return size
            }
        }
        // 获取接近值
        val map = hashMapOf<Float, Size>()
        outputSizes?.forEach { size ->
            val aspectRatio: Float = if (screenAspectRatio > 1) {
                size.width.toFloat() / size.height.toFloat()
            } else {
                size.height.toFloat() / size.width.toFloat()
            }
            val ratioDiff = abs(aspectRatio - screenAspectRatio)
            if (map[ratioDiff] != null) {
                map[ratioDiff]?.width?.also {
                    if (it < size.width) {
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
            if (size.width == 1080 && size.height == 1920) {
                return size
            }
            if (size.width == 1920 && size.height == 1080) {
                return size
            }
        }
        outputSizes?.forEach { size ->
            if (size.width == 1280 && size.height == 720) {
                return size
            }
            if (size.width == 720 && size.height == 1280) {
                return size
            }
        }
        return bestSize
    }

    private fun createCameraPreviewSession(camera: CameraDevice, surface: Surface, bestSize: Size) {
        try {
            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            // 设置自动对焦模式
            // captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO) // 自动对焦
            // 设置其他参数，例如曝光、白平衡等
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)

            val previewRequest = captureRequestBuilder.build()

            camera.createCaptureSession(
                listOf(surface, imageReader?.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        // 启动预览
                        captureSession?.setRepeatingRequest(previewRequest, null, backgroundHandler)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(
                            this@CameraActivity,
                            "Failed to configure camera",
                            Toast.LENGTH_SHORT
                        ).show()
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
            val captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            imageReader?.surface?.let { captureRequestBuilder?.addTarget(it) }

            // 镜头是否需要旋转
            var adjustedOrientation = 0
            cameraId.let {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

                // 获取设备的旋转角度
                val displayRotation = windowManager.defaultDisplay.rotation
                val rotationDegrees = getRotationDegrees(displayRotation)

                // 判断镜头方向
                when (lensFacing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> {
                        // 前置摄像头
                        adjustedOrientation = (sensorOrientation + rotationDegrees) % 360
                        // 处理前置摄像头的调整
                    }
                    CameraCharacteristics.LENS_FACING_BACK -> {
                        // 后置摄像头
                        adjustedOrientation = (sensorOrientation + rotationDegrees) % 360
                        // 处理后置摄像头的调整
                    }
                }
            }
            // 设置拍照方向
            captureRequestBuilder?.set(CaptureRequest.JPEG_ORIENTATION, adjustedOrientation)
            Timber.d("onCaptureCompleted adjustedOrientation=${adjustedOrientation}")
            Timber.d("onCaptureCompleted thread=${Thread.currentThread()}")
            // 拍照并保存
            val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        // saveImage(result)
                    }
                }
            }

            captureSession?.capture(
                captureRequestBuilder?.build()!!,
                captureCallback,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun saveImage(result: TotalCaptureResult?) {
        // 这里保存照片
        val image = imageReader?.acquireLatestImage()
        Timber.d("onCaptureCompleted image ${image}")
        if (image != null) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            val file = File(
                getExternalFilesDir(null),
                "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
            )
            try {
                Timber.d("onCaptureCompleted outputStream ${file.absolutePath}")
                val outputStream = FileOutputStream(file)
                outputStream.write(bytes)
                outputStream.close()
                textureView.post {
                    Toast.makeText(this, "Photo saved to ${file.absolutePath}", Toast.LENGTH_SHORT)
                        .show()
                }
                Timber.d("onCaptureCompleted Photo saved to ${file.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }finally {
                image.close()
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

fun getBestPreviewOrder(
    sizes: List<PreSize>,
    desired: PreSize, isFitXY: Boolean
): List<PreSize> {

    Collections.sort<PreSize>(sizes) { a, b ->
        if (isFitXY) {
            val aScore: Float = getFitXYScore(a, desired)
            val bScore: Float = getFitXYScore(b, desired)
            // Bigger score first
            bScore.compareTo(aScore)
        } else {
            val aScore: Float = getFitCenterScore(a, desired)
            val bScore: Float = getFitCenterScore(b, desired)
            // Bigger score first
            bScore.compareTo(aScore)
        }
    }
    return sizes
}

private fun getFitCenterScore(
    size: PreSize,
    desired: PreSize
): Float {
    if (size.width <= 0 || size.height <= 0) {
        return 0f
    }
    val scaled = size.scaleFit(desired)
    // Scaling preserves aspect ratio
    val scaleRatio = scaled.width * 1.0f / size.width

    // Treat downscaling as slightly better than upscaling
    val scaleScore = if (scaleRatio > 1.0f) {
        // Upscaling
        (1.0 / scaleRatio).pow(1.1).toFloat()
    } else {
        // Downscaling
        scaleRatio
    }

    // Ratio of scaledDimension / dimension.
    // Note that with scaleCrop, only one dimension is cropped.
    val cropRatio = (desired.width * 1.0f / scaled.width) *
            (desired.height * 1.0f / scaled.height)

    // Cropping is very bad, since it's used-visible for centerFit
    // 1.0 means no cropping.
    val cropScore = 1.0f / cropRatio / cropRatio / cropRatio

    return scaleScore * cropScore
}

private fun getFitXYScore(
    size: PreSize,
    desired: PreSize
): Float {
    if (size.width <= 0 || size.height <= 0) {
        return 0f
    }
    val scaleX: Float =
        absRatio(size.width * 1.0f / desired.width)
    val scaleY: Float =
        absRatio(size.height * 1.0f / desired.height)

    val scaleScore = 1.0f / scaleX / scaleY

    val distortion: Float =
        absRatio((1.0f * size.width / size.height) / (1.0f * desired.width / desired.height))

    // Distortion is bad!
    val distortionScore = 1.0f / distortion / distortion / distortion

    return scaleScore * distortionScore
}

private fun absRatio(ratio: Float): Float {
    return if (ratio < 1.0f) {
        1.0f / ratio
    } else {
        ratio
    }
}
