package com.example.u.scan_code;

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.u.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import timber.log.Timber


class ScanZxingActivity : AppCompatActivity(), DecoratedBarcodeView.TorchListener {

    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeView: DecoratedBarcodeView

    private val REQUEST_CAMERA_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing)

        barcodeView = findViewById(R.id.zxing_barcode_scanner)

//        // 设置扫描配置，指定只扫描条形码
//        val formats = listOf(
//            BarcodeFormat.EAN_13, // EAN-13 条形码
//            BarcodeFormat.UPC_A,  // UPC-A 条形码
//            BarcodeFormat.CODE_128, // Code 128 条形码
//            BarcodeFormat.CODE_39, // Code 39 条形码
//            BarcodeFormat.ITF, // ITF 条形码
//            BarcodeFormat.CODABAR // Codabar 条形码
//        )
//
//        // 设置扫描格式
//        barcodeView.barcodeView.setDecoderFactory {
//            formats.map { it.createDecoder() }
//        }

        barcodeView.setTorchListener(this)
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)


        barcodeView.decodeContinuous {
            it?.let {
                onScanResult(it.result)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            captureManager.onResume()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureManager.onResume()
            } else {
                // Handle permission denial
                finish()
            }
        }
    }

    override fun onTorchOn() {
        Timber.i("ScanActivity, Torch on")
    }

    override fun onTorchOff() {
        Timber.i("ScanActivity, Torch off")
    }

    // Callback method for barcode scan results
    private fun onScanResult(result: Result) {
        if (result.barcodeFormat in listOf(BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.CODE_128, BarcodeFormat.CODE_39)){
            val scanResult = result.text
            // 扫描结果返回
            val intent = intent
            intent.putExtra("scan_result", scanResult)
            setResult(RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, "扫描结果: 这个不是条形码", Toast.LENGTH_SHORT).show()
        }
    }
}
