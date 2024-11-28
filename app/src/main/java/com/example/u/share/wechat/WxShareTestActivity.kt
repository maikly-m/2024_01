package com.example.u.share.wechat

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.u.R
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


class WxShareTestActivity : AppCompatActivity() {
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
            shareToWeChat()
        }
    }

    private fun shareToWeChat() {
        val textObj = WXTextObject()
        textObj.text = "我正在使用微信分享功能！"

        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        msg.description = "分享文本"

        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession // WXSceneSession: 发送到微信好友, WXSceneTimeline: 发送到朋友圈

        api.sendReq(req)
    }
}
