package com.example.u.scan_code

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.u.R

class ScanTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_test)

        val scanButton: Button = findViewById(R.id.btn_staret)
        scanButton.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivityForResult(intent, 100)  // 100是请求码
        }
    }

    // 处理扫描结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val scanResult = data?.getStringExtra("scan_result")
            if (scanResult != null) {
                Toast.makeText(this, "扫描结果: $scanResult", Toast.LENGTH_LONG).show()
                // 在这里处理扫描结果，例如跳转到另一个页面，或进行其他操作
                val tv: TextView = findViewById(R.id.tv)
                tv.setText(scanResult)
            }
        }
    }
}
