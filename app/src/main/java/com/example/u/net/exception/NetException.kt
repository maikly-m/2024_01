package com.example.u.net.exception

import android.net.ParseException
import com.google.gson.JsonParseException

import org.json.JSONException
import retrofit2.HttpException

import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLHandshakeException


object NetException {

    fun handle(t: Throwable): ResultErr {
        val ex: ResultErr
        if (t is ResultErr) {
            ex = t
        } else if (t is HttpException) {
            ex = when (t.code()) {
                ApiResultCode.UNAUTHORIZED,
                ApiResultCode.FORBIDDEN,
                    //权限错误，需要实现
                ApiResultCode.NOT_FOUND -> ResultErr(
                    t.code().toString(),
                    "网络错误"
                )
                ApiResultCode.REQUEST_TIMEOUT,
                ApiResultCode.GATEWAY_TIMEOUT -> ResultErr(
                    t.code().toString(),
                    "网络连接超时"
                )
                ApiResultCode.INTERNAL_SERVER_ERROR,
                ApiResultCode.BAD_GATEWAY,
                ApiResultCode.SERVICE_UNAVAILABLE -> ResultErr(
                    t.code().toString(),
                    "服务器错误"
                )
                else -> ResultErr(t.code().toString(), "网络错误")
            }
        } else if (t is JsonParseException
            || t is JSONException
            || t is ParseException
        ) {
            ex = ResultErr(
                ApiResultCode.PARSE_ERROR,
                "解析错误"
            )
        } else if (t is SocketException) {
            ex = ResultErr(
                ApiResultCode.REQUEST_TIMEOUT.toString(),
                "网络连接错误，请重试"
            )
        } else if (t is SocketTimeoutException) {
            ex = ResultErr(
                ApiResultCode.REQUEST_TIMEOUT.toString(),
                "网络连接超时"
            )
        } else if (t is SSLHandshakeException) {
            ex = ResultErr(
                ApiResultCode.SSL_ERROR,
                "证书验证失败"
            )
            return ex
        } else if (t is UnknownHostException) {
            ex = ResultErr(
                ApiResultCode.UNKNOWN_HOST,
                "网络错误，请切换网络重试"
            )
            return ex
        } else if (t is UnknownServiceException) {
            ex = ResultErr(
                ApiResultCode.UNKNOWN_HOST,
                "网络错误，请切换网络重试"
            )
        } else if (t is NumberFormatException) {
            ex = ResultErr(
                ApiResultCode.UNKNOWN_HOST,
                "数字格式化异常"
            )
        } else {
            ex = ResultErr(
                ApiResultCode.UNKNOWN,
                "未知错误"
            )
        }
        return ex
    }
}