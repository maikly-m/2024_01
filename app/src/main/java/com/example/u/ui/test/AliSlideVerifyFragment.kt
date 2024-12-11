package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.databinding.FragmentAliSlideVerifyBinding
import timber.log.Timber


class AliSlideVerifyFragment : Fragment() {

    private var _binding: FragmentAliSlideVerifyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(AliSlideVerifyViewModel::class.java)

        _binding = FragmentAliSlideVerifyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView()
        return root
    }

    private fun initView() {
        // 页面布局。
        val webView = binding.webview
        // 设置屏幕自适应。
        webView.getSettings().useWideViewPort = true
        webView.getSettings().loadWithOverviewMode = true
        // 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的滑动验证组件进行对抗。
        webView.getSettings().cacheMode = WebSettings.LOAD_NO_CACHE
        // 设置不使用默认浏览器，而直接使用WebView组件加载页面。
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        })
        // 设置WebView组件支持加载JavaScript。
        webView.getSettings().setJavaScriptEnabled(true)
        // 建立JavaScript调用Java接口的桥梁。
        webView.addJavascriptInterface(TestJsInterface(), "testInterface")
        // 加载业务页面。
        webView.loadUrl("file:///android_asset/slide_verify_test.html")
//        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class TestJsInterface {
        @JavascriptInterface
        fun getSlideData(callData: String?) {
            Timber.d("getSlideData $callData")
        }
        @JavascriptInterface
        fun sendMessage(message: String) {
            Timber.d("sendMessage $message")
        }
    }
}