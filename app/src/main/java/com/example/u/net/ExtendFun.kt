package com.example.u.net

import android.text.TextUtils
import com.example.u.net.model.BaseModel
import com.example.u.uitls.gson.GsonUtil
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun <reified T> Call<ResponseBody>.awaitCall(): BaseModel<T>{
    return suspendCoroutine { continuation ->
        try {
            // 在后台线程执行 Retrofit 请求
            enqueue(object : Callback<ResponseBody> {
                override fun onResponse(p0: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // 解析
                        try {
                            val string = response.body()?.string()
                            if (TextUtils.isEmpty(string)){
                                continuation.resumeWithException(Throwable("string null "))
                            }else{
                                string.let {
                                    val typeToken = object : TypeToken<BaseModel<T>>() {}.type
                                    Timber.e("body ${it}")
//                            val json = "{\"value\":\"Hello\"}"
                                    val bean = GsonUtil.gson.fromJson<BaseModel<T>>(it, typeToken)
                                    continuation.resume(bean)
                                }
                            }
                        } catch (e: JsonSyntaxException) {
                            continuation.resumeWithException(e)
                        }
                    } else {
                        // 处理失败
                        continuation.resumeWithException(Throwable("Error: ${response.errorBody()?.string()}"))
                    }
                }

                override fun onFailure(p0: Call<ResponseBody>, p1: Throwable) {
                    continuation.resumeWithException(p1)
                }

            })

        } catch (e: Exception) {
            // 捕获异常并通过 continuation 恢复异常状态
            continuation.resumeWithException(e)
        }
    }
}

