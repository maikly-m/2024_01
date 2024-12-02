package com.example.u

import android.graphics.Color
import android.os.Bundle
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.u.databinding.ActivityMainBinding
import com.example.u.uitls.setFullScreen
import com.example.u.uitls.setStatusBarAndNavBar
import com.example.u.uitls.showStatusBarAndNavBar
import com.example.u.uitls.showStatusBarAndNavBarFull
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏
        setStatusBarAndNavBar(window, Color.WHITE, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}