package com.example.u.scan_code;


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.u.R
import com.google.zxing.Result
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import com.journeyapps.barcodescanner.camera.CameraSettings

class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeView: BarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing_custom)

        // 获取 BarcodeView 控件
        barcodeView = findViewById(R.id.zxing_barcode_scanner)

        // 获取扫描框
        val viewfinderView: ViewfinderView = findViewById(R.id.zxing_barcode_viewfinder)

        // todo 不使用viewfinder
        // 设置扫描框的颜色，样式等
        // viewfinderView.setMaskColor(resources.getColor(android.R.color.holo_blue_light))
        // viewfinderView.setCameraPreview(barcodeView)

        // 配置扫描参数
        val cameraSettings = CameraSettings()
        cameraSettings.isAutoFocusEnabled = true  // 开启自动对焦
        barcodeView.cameraSettings = cameraSettings

        // 设置 BarcodeView 的扫描格式 (可以自定义支持的条形码格式)
        barcodeView.decodeContinuous { result ->
            // 处理扫描结果
            if (result != null) {
                handleResult(result.result)
            }
        }
        // barcodeView.setTorch()
    }

    private fun handleResult(result: Result) {
        val scanResult = result.text
        Toast.makeText(this, "扫描结果: $scanResult", Toast.LENGTH_LONG).show()
        // 返回扫描结果
        val intent = intent
        intent.putExtra("scan_result", scanResult)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // 启动扫描
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        // 停止扫描
        barcodeView.pause()
    }
}

