package com.example.u

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.u.databinding.ActivityMainBinding
import com.example.u.uitls.setStatusBarAndNavBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏
        setStatusBarAndNavBar(window, Color.WHITE, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val findNavController = findNavController(R.id.nav_host_fragment_activity_main)

        // 设置监听器，监听每次导航变化
        findNavController.addOnDestinationChangedListener { controller, destination, arguments ->
            // 在每次页面跳转时进行 token 检查
            // 记录当前页面的导航栈，或者直接记录当前页面的目标 Fragment
            val currentDestination = findNavController.currentDestination
            if (isTokenInvalid()) {
                // 如果 token 无效，跳转到登录页面
                // controller.navigate(R.id.loginFragment)
                // 需要将currentDestination传递到loginFragment，然后登陆成功，再切到currentDestination
            }
        }
    }

    private fun isTokenInvalid(): Boolean {
        return false
    }
}


