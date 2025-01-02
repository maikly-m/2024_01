package com.example.u

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.u.databinding.ActivityTransparentBinding
import com.example.u.uitls.setStatusBarAndNavBar

class TransparentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransparentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏
        setStatusBarAndNavBar(window, Color.WHITE, true)

        binding = ActivityTransparentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}


