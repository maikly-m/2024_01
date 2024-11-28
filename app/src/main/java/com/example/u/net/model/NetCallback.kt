package com.example.u.net.model

import com.example.u.net.exception.ResultErr


sealed class NetCallback<out T : Any> {

    data class Success<out T : Any>(val data: T) : NetCallback<T>()

    data class Error(val exception: ResultErr) : NetCallback<Nothing>()


}