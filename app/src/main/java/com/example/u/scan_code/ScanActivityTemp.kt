//package com.example.u.scan_code
//
//import android.os.Bundle
//import android.widget.Toast
//import com.journeyapps.barcodescanner.CaptureActivity
//
//class ScanActivity : CaptureActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 如果你需要自定义扫描界面，这里可以做进一步的设置
//
//    }
//
//
//
//    fun handleResult(result: com.google.zxing.Result?) {
//        if (result != null) {
//            val scanResult = result.text
//            Toast.makeText(this, "扫描结果: $scanResult", Toast.LENGTH_LONG).show()
//
//            // 扫描结果返回
//            val intent = intent
//            intent.putExtra("scan_result", scanResult)
//            setResult(RESULT_OK, intent)
//            finish()
//        } else {
//            Toast.makeText(this, "扫描失败，请重试", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
