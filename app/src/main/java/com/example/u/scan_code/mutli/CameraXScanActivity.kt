package com.example.u.scan_code.mutli

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.u.R
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.Executors

class CameraXScanActivity : AppCompatActivity() {

    private lateinit var scanTest: Button
    private lateinit var scanner: BarcodeScanner
    private lateinit var previewView: PreviewView
    private lateinit var overlayView: OverlayView
    private var isScanning = true // 控制扫描状态
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scan_camerax)

        previewView = findViewById(R.id.previewView)
        overlayView = findViewById(R.id.overlayView)
        scanTest = findViewById(R.id.scan_test)
        scanTest.setOnClickListener {
            overlayView.reset()
            startCamera()
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
                Barcode.FORMAT_CODE_128)
            .build()
        scanner = BarcodeScanning.getClient(options)

        findViewById<Button>(R.id.gallery_test).setOnClickListener {
            overlayView.reset()
            openGallery()
        }
    }

    // 获取系统相册图片
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uriToBitmap(this, it)?.run {
                processBitmapImage(this)
            }
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            // 获取 ContentResolver
            val contentResolver: ContentResolver = context.contentResolver
            // 打开输入流并转换为 Bitmap
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getMimeType(uri: Uri, context: Context): String? {
        val contentResolver: ContentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }

    private fun startCamera() {
        isScanning = true
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Timber.e("CameraX, Error starting camera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        if (!isScanning) {
            imageProxy.close() // 直接关闭
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()){
                        if (barcodes.size > 0) {
                            for (barcode in barcodes) {
                                Timber.d("MLKit , Barcode: ${barcode.rawValue}")
                            }
                            val rects = barcodes.mapNotNull { it.boundingBox }
                            rects.let {
                                imageToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)?.run {
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
                        stopCamera()
                        stopScanning()
                    }
                }
                .addOnFailureListener { e ->
                    Timber.e("MLKit, Error: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close() // 确保关闭 ImageProxy，避免 maxImages 错误
                }
        }
    }

    private fun processBitmapImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        Timber.d("MLKit , processBitmapImage: ")
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()){
                    if (barcodes.size > 1) {
                        for (barcode in barcodes) {
                            Timber.d("MLKit , Barcode: ${barcode.rawValue}")
                        }
                        val rects = barcodes.mapNotNull { it.boundingBox }
                        rects.let {
                            overlayView.setBarcodeRects(bitmap, it)
                        }
                    } else {
                        for (barcode in barcodes) {
                            Timber.d("MLKit , one Barcode: ${barcode.rawValue}")
                        }
                        // todo 直接处理
                    }
                }
            }
            .addOnFailureListener { e ->
                Timber.e("MLKit, Error: ${e.message}")
            }
            .addOnCompleteListener {
            }
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll() // 解绑所有用例，停止摄像头
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopScanning() {
        isScanning = false
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
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
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
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


}

