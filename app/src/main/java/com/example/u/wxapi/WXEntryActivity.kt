package com.example.u.wxapi

import android.app.Activity
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory


class WXEntryActivity : Activity(), IWXAPIEventHandler {
    private lateinit var api: IWXAPI
    // todo
    private val APP_ID = "replace_your_app_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化微信 SDK
        api = WXAPIFactory.createWXAPI(this, APP_ID, true)
        api.handleIntent(intent, this)
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(resp: BaseResp?) {
        if (resp is SendAuth.Resp) {
            // 获取授权结果
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                val code = resp.code  // 获取授权码
                // 在这里可以通过 code 获取 access_token
                getAccessToken(code)
            } else {
                // 登录失败，做相应处理
            }
        }
    }

    private fun getAccessToken(code: String) {
        // 在此进行网络请求，通过授权码获取 access_token 和 openid
        // 使用 OkHttp 或其他网络库请求微信接口
    }
}
