package com.example.u.login.qq

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.u.R
import com.tencent.connect.common.Constants
import com.tencent.open.utils.HttpUtils
import com.tencent.tauth.Tencent
import com.tencent.tauth.IRequestListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class QQLoginActivity : Activity() {

    private lateinit var mTencent: Tencent
    private val APP_ID = "replace_your_app_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qq_entry_test)
        // 初始化腾讯QQ SDK
        mTencent = Tencent.createInstance(APP_ID, applicationContext)

        // 登录按钮点击事件
        findViewById<Button>(R.id.btn_qq).setOnClickListener {
            loginWithQQ()
        }
    }

    // 发起QQ登录请求
    private fun loginWithQQ() {
        val listener = object : IUiListener {
            override fun onComplete(response: Any?) {
                val jsonResponse = response as JSONObject
                val accessToken = jsonResponse.optString("access_token")
                val openid = jsonResponse.optString("openid")

                // 登录成功后，获取用户信息
                getUserInfo(accessToken, openid)
            }

            override fun onError(uiError: UiError?) {
                // 登录失败，做相应处理
            }

            override fun onCancel() {
                // 登录取消，做相应处理
            }

            override fun onWarning(p0: Int) {
                TODO("Not yet implemented")
            }
        }

        // 发起QQ登录请求
        mTencent.login(this, "all", listener)
    }

    // 获取QQ用户信息
    private fun getUserInfo(accessToken: String, openid: String) {
        val params = Bundle().apply {
            putString("access_token", accessToken)
            putString("openid", openid)
        }

        // 发起获取用户信息请求
        mTencent.requestAsync("user/get_user_info", params, "GET", object : IRequestListener {
            override fun onComplete(jsonResponse: JSONObject?) {
                val nickname = jsonResponse?.optString("nickname")
                val figureUrl = jsonResponse?.optString("figureurl_2")

                // 在此处理用户信息，比如保存到本地数据库，或者展示到界面
            }

            override fun onIOException(e: java.io.IOException?) {
                // 处理网络请求失败的情况
            }

            override fun onMalformedURLException(p0: MalformedURLException?) {
                // 获取用户信息失败，做相应处理
            }

            override fun onJSONException(p0: JSONException?) {
                // 获取用户信息失败，做相应处理
            }

            override fun onSocketTimeoutException(p0: SocketTimeoutException?) {
                // 获取用户信息失败，做相应处理
            }

            override fun onNetworkUnavailableException(p0: HttpUtils.NetworkUnavailableException?) {
                // 获取用户信息失败，做相应处理
            }

            override fun onHttpStatusException(p0: HttpUtils.HttpStatusException?) {
                // 获取用户信息失败，做相应处理
            }

            override fun onUnknowException(p0: java.lang.Exception?) {
                // 获取用户信息失败，做相应处理
            }
        })
    }


}
