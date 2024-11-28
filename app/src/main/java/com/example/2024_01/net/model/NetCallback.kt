package com.example.`2024_01`.net.model

import com.example.`2024_01`.net.exception.ResultErr


sealed class NetCallback<out T : Any> {

    data class Success<out T : Any>(val data: T) : NetCallback<T>()

    data class Error(val exception: ResultErr) : NetCallback<Nothing>()


}