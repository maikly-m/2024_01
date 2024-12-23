package com.example.u.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.YuvImage
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
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
import com.example.u.R
import com.example.u.camera.preview.PreSize
import com.example.u.scan_code.mutli.OverlayView
import com.example.u.uitls.setFullScreen
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

class Camera2ScanActivity : AppCompatActivity() {

    private var skipCount: Int = 0
    private lateinit var textureView: TextureView
    private lateinit var scanTest: Button

    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private lateinit var handler: Handler
    private lateinit var backgroundHandler: Handler
    private var cameraId: String = ""
    private lateinit var scanner: BarcodeScanner
    private lateinit var overlayView: OverlayView
    private var isScanning = true // 控制扫描状态
    private val executor = Executors.newSingleThreadExecutor()

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

        setContentView(R.layout.activity_custom_scan_camera2)

        textureView = findViewById(R.id.previewView)
        scanTest = findViewById(R.id.scan_test)
        overlayView = findViewById(R.id.overlayView)
        // overlayView.visibility = View.GONE

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

        /**
         * 常见条形码类型
         * QR Code (QR_CODE):
         *
         * 用于存储 URL、文本等信息。
         * 较为常见，广泛应用于支付、信息交换等。
         * EAN-13 (EAN_13) 和 EAN-8 (EAN_8):
         *
         * 用于商品零售条形码，EAN-13 是 13 位数字，而 EAN-8 是 8 位数字。
         * 主要用于商品标识。
         * UPC-A (UPC_A) 和 UPC-E (UPC_E):
         *
         * UPC-A 是常见的美国商品条形码，包含 12 位数字。
         * UPC-E 是 UPC-A 的一种简化版本，通常用于小包装商品。
         * Code 128 (CODE_128) 和 Code 39 (CODE_39):
         *
         * Code 128 和 Code 39 是常见的工业应用条形码，广泛应用于运输、物流等领域。
         * Data Matrix (DATA_MATRIX):
         *
         * 适用于小型物品，常用于零件标识和医药行业。
         * PDF417 (PDF417):
         *
         * 用于存储大量信息，常见于身份证、驾照和票证等。
         * Aztec (AZTEC):
         *
         * 适用于小尺寸条形码，通常用于交通票证等。
         * Codabar (CODABAR):
         *
         * 一种老式条形码，曾广泛用于库房管理、快递行业等。
         */
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_128
            )
            .build()
        scanner = BarcodeScanning.getClient(options)

        scanTest.setOnClickListener {
            overlayView.reset()
            startCamera()
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
                openCamera2(surface)
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
    private fun openCamera2(surface: SurfaceTexture) {
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

            Timber.d("bestSize = $bestSize")
            Timber.d("size = $size")

            imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 2)
            imageReader!!.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                processImage(image)
//                if (skipCount % 3 == 0){
//                }else{
//                    image.close()
//                }
                skipCount++
            }, backgroundHandler)

            // 打开相机
            if (ActivityCompat.checkSelfPermission(
                    this@Camera2ScanActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            // 配置输出尺寸为最佳尺寸
            // val previewSize = bestSize
            val previewSize = size
            //updateTextureViewTransform(textureView.height, textureView.width, bestSize.width, bestSize.height)

            textureView.layoutParams.let {
                it.width = previewSize.height
                it.height = previewSize.width
                textureView.layoutParams = it
            }

            overlayView.layoutParams.let {
                it.width = previewSize.height
                it.height = previewSize.width
                overlayView.layoutParams = it
            }

            Timber.e("surface ${surface}")
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    surface.setDefaultBufferSize(previewSize.width, previewSize.height)
                    cameraDevice = camera
                    val s = Surface(surface)
                    createCameraPreviewSession(camera, s)
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

    private fun processImage(image: Image) {
        if (!isScanning) {
            image.close()
            return
        }
        if (taskDoing){
            image.close()
            return
        }
        taskDoing = true
        // 将 Camera2 的 Image 转换为 ML Kit 的 InputImage
        val rotationDegrees = 90 // 根据设备方向调整
        Timber.d("image ${image.width}x${image.height}")
        val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
        Timber.d("inputImage ${inputImage.width}x${inputImage.height}")

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                Timber.e("MLKit, barcodes ...")
                if (barcodes.isNotEmpty()) {
                    if (barcodes.size > 0) {
                        for (barcode in barcodes) {
                            Timber.d("MLKit , Barcode: ${barcode.rawValue}")
                        }
                        Timber.d("MLKit , mediaImage: ${image.width}x${image.height}")
                        val rects = barcodes.mapNotNull { it.boundingBox }
                        rects.let {
                            inputImageToBitmap(inputImage)?.run {
                                overlayView.post {
                                    overlayView.setBarcodeRects(this, it)
                                }
                            }
                        }
                    } else {
                        for (barcode in barcodes) {
                            Timber.d("MLKit , one Barcode: ${barcode.rawValue}")
                        }
                        // todo 直接处理
                    }
                    stopScan()
                    stopScanning()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
            .addOnCompleteListener {
                Timber.e("MLKit, Complete ...")
                taskDoing = false
                image.close()
            }
    }

    private fun imageToBitmap(image: Image, rotationDegrees: Int): Bitmap? {
        if (image.format != ImageFormat.YUV_420_888) {
            // throw IllegalArgumentException("Image format must be YUV_420_888")
            return null
        }

        val yBuffer = image.planes[0].buffer // Y 平面
        val uBuffer = image.planes[1].buffer // U 平面
        val vBuffer = image.planes[2].buffer // V 平面

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        // 创建存储 NV21 格式数据的字节数组
        val nv21 = ByteArray(ySize + uSize + vSize)

        // 将 YUV 数据复制到数组中
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // 获取宽高
        val width = image.width
        val height = image.height

        // 使用 YuvImage 转换为 Bitmap
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out) // 质量为 100%
        val jpegBytes = out.toByteArray()

        // 将 JPEG 数据解码为 Bitmap
        val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

        // 旋转 Bitmap
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun inputImageToBitmap(inputImage: InputImage): Bitmap? {
        val mediaImage = inputImage.mediaImage // 获取底层的 MediaImage
        if (mediaImage != null) {
            val rotationDegrees = inputImage.rotationDegrees
            val bitmap = mediaImageToBitmap(mediaImage, rotationDegrees)
            return bitmap
        }
        return null
    }
    private fun mediaImageToBitmap(mediaImage: Image, rotationDegrees: Int): Bitmap {
        val yBuffer = mediaImage.planes[0].buffer
        val uBuffer = mediaImage.planes[1].buffer
        val vBuffer = mediaImage.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, mediaImage.width, mediaImage.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, mediaImage.width, mediaImage.height), 100, out)
        val jpegBytes = out.toByteArray()

        val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

        // 旋转 Bitmap
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    var taskDoing = false
    private fun processInputImage(image: InputImage) {

    }

    private fun updateTextureViewTransform(viewWidth: Int, viewHeight: Int, previewWidth: Int, previewHeight: Int) {
        val matrix = Matrix()

        // 计算图像的宽高比
        val scaleX = viewWidth.toFloat() / previewWidth.toFloat()
        val scaleY = viewHeight.toFloat() / previewHeight.toFloat()

        // 将宽高比不匹配的情况进行缩放
        matrix.postScale(scaleX, scaleY)

        // 如果需要，可以对齐居中或者调整位置
        matrix.postTranslate(
            (viewWidth - previewWidth * scaleX) / 2f,
            (viewHeight - previewHeight * scaleY) / 2f
        )

        // 将变换矩阵应用到TextureView
        textureView.setTransform(matrix)
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

    private fun createCameraPreviewSession(camera: CameraDevice, surface: Surface) {
        try {
            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            imageReader?.surface?.let {
                captureRequestBuilder.addTarget(it)
            }
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
                            this@Camera2ScanActivity,
                            "Failed to configure camera",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun stopScanning() {
        isScanning = false
    }

    private fun stopScan(): Unit {
        cameraDevice?.close()
        captureSession?.close()
        imageReader?.close()
    }

    override fun onResume() {
        super.onResume()
        backgroundHandler = Handler(Looper.myLooper()!!)
        if (ActivityCompat.checkSelfPermission(
                this@Camera2ScanActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
           if (textureView.isAvailable){
               Timber.d("onResume")
               val matrix = Matrix()
               matrix.postRotate(90f, textureView.width / 2f, textureView.height / 2f)
               textureView.setTransform(matrix)

               textureView.surfaceTexture?.let {
                   openCamera2(it)
               }
           }
        }
    }

    override fun onPause() {
        super.onPause()
        stopScan()
    }


}







