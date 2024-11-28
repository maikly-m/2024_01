package com.example.u.uitls

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 全屏显示，状态栏及导航栏均隐藏,均不占位
 *
 * <p>View.SYSTEM_UI_FLAG_LAYOUT_STABLE：全屏显示时保证尺寸不变。
 * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，状态栏显示在Activity页面上面。
 * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏导航栏
 * View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
 * View.SYSTEM_UI_FLAG_VISIBLE：Activity非全屏显示，显示状态栏和导航栏。
 * View.INVISIBLE：Activity伸展全屏显示，隐藏状态栏。
 * View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY：必须配合View.SYSTEM_UI_FLAG_FULLSCREEN和View
 * .SYSTEM_UI_FLAG_HIDE_NAVIGATION组合使用，达到的效果是拉出状态栏和导航栏后显示一会儿消失。
 *
 * @param window
 */
fun setFullScreen(window: Window?): Pair<Int, Int>? {
    if (window == null) return null
    val attributes = window.attributes
    var oldCutoutMode = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        oldCutoutMode = attributes.layoutInDisplayCutoutMode
        attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = attributes
    }
    val oldOption = window.decorView.systemUiVisibility
    val option = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
    window.decorView.systemUiVisibility = option
    window.decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
        if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            window.decorView.systemUiVisibility = option
        }
    }
    return Pair(oldCutoutMode, oldOption)
}

fun recover(window: Window?, config: androidx.core.util.Pair<Int?, Int>) {
    if (window == null) return
    val attributes = window.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        attributes.layoutInDisplayCutoutMode = config.first!!
        window.attributes = attributes
    }
    val option = config.second
    window.decorView.systemUiVisibility = option
    window.decorView.setOnSystemUiVisibilityChangeListener { visibility: Int -> }
}

fun showStatusBarAndNavBarFull(window: Window) {
    // 不隐藏状态栏和导航栏，状态栏在页面之上,页面是全屏显示，导航栏浮在页面上
    val option = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

    window.decorView.systemUiVisibility = option
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // 清除背景
        window.isNavigationBarContrastEnforced = false
    }
    window.navigationBarColor = Color.TRANSPARENT
}

fun showStatusBarAndNavBar(window: Window, color: Int) {
    // 不隐藏状态栏和导航栏，状态栏在页面之上,还需要设置相应的背景颜色值
    val option = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    window.decorView.systemUiVisibility = option
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = color
}

fun setStatusBarAndNavBar(window: Window, color: Int?, isBlack: Boolean?) {
    // 不隐藏状态栏和导航栏，状态栏在页面之上,还需要设置相应的背景颜色值
    var option = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (isBlack != null) {
        if (isBlack) {
            option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // nothing
        }
    }
    window.decorView.systemUiVisibility = option
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = Color.TRANSPARENT
    if (color != null) {
        window.navigationBarColor = color
    }
}