package com.example.u.login.wechat

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.u.R
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


class WxEntryTestActivity : AppCompatActivity() {
    private lateinit var api: IWXAPI
    private val APP_ID = "replace_your_app_id"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wx_entry_test)

        // 初始化微信 API
        api = WXAPIFactory.createWXAPI(this, APP_ID, false)
        api.registerApp(APP_ID)

        // 设置微信登录按钮的点击事件
        findViewById<Button>(R.id.btn_wx).setOnClickListener {
            sendLoginReq()
        }
    }

    private fun sendLoginReq() {
        val req = SendAuth.Req().apply {
            scope = "snsapi_login"  // 授权范围
            state = "wechat_sdk_demo_test"  // 防止 CSRF 攻击的随机字符串
        }
        api.sendReq(req)
    }

}
