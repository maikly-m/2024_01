package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.databinding.FragmentNotificationsBinding
import com.example.u.databinding.FragmentRichTextBinding
import com.example.u.ui.notifications.NotificationsViewModel
import org.jsoup.Jsoup

class RichTextFragment : Fragment() {

    private var _binding: FragmentRichTextBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(RichTextViewModel::class.java)

        _binding = FragmentRichTextBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // 让 WebView 支持 JavaScript 等特性
        val webSettings = binding.web.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        // 初始 HTML 内容
        val htmlContent = """
            <html>
                <body>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <h1>欢迎来到 Android</h1>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 <b>加粗</b> 和 <i>斜体</i> 的文本。</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <p>这是 66666666666666 6666666 \n 6666666 \n 7777777</p>
                    <a href="https://www.google.com">点击这里访问 Google</a>
                </body>
            </html>
        """
        // 使用 Jsoup 解析 HTML 内容
        val document = Jsoup.parse(htmlContent)
        // 你可以进行一些修改，比如修改链接文本
        document.select("a").attr("href", "https://www.baidu.com")
        // 获取修改后的 HTML
        val modifiedHtml = document.html()
        // 加载修改后的 HTML 内容到 WebView
        binding.web.loadDataWithBaseURL(null, modifiedHtml, "text/html", "UTF-8", null)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}