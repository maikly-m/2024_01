package com.example.u.share.qq

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.u.R
import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.open.utils.HttpUtils
import com.tencent.tauth.Tencent
import com.tencent.tauth.IRequestListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class QQShareActivity : Activity() {

    private lateinit var mTencent: Tencent
    private val APP_ID = "replace_your_app_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qq_entry_test)
        // 初始化腾讯QQ SDK
        mTencent = Tencent.createInstance(APP_ID, applicationContext)

        // 登录按钮点击事件
        findViewById<Button>(R.id.btn_qq).setOnClickListener {
            shareToQQ()
        }
    }

    private fun shareToQQ() {
        val params = Bundle()
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享标题")
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享内容")
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://www.qq.com")
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "https://www.example.com/sample.jpg")
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称")

        mTencent.shareToQQ(this, params, object : IUiListener {
            override fun onComplete(response: Any) {
                // 分享成功
            }

            override fun onError(e: UiError) {
                // 分享失败
            }

            override fun onCancel() {
                // 分享取消
            }

            override fun onWarning(p0: Int) {
                TODO("Not yet implemented")
            }
        })
    }


}
