package com.example.u.net.net

import com.example.u.net.exception.NetException

import com.example.u.net.exception.ResultErr
import com.example.u.net.model.BaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import com.example.u.net.model.NetCallback

open class BaseRepository {
    private val TAG = "BaseRepository"

    suspend fun <T : Any> callRequest(
        call: suspend () -> NetCallback<T>
    ): NetCallback<T> {
        return try {
            call()
        } catch (e: Exception) {
            //这里统一处理异常
            e.printStackTrace()
            NetCallback.Error(NetException.handle(e))
        }
    }

    suspend fun <T : Any> handleResponse(
        response: BaseModel<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): NetCallback<T> {
        return coroutineScope {
            // 处理返回数据
            if (response.errorCode == -1) {
                errorBlock?.let { it() }
                NetCallback.Error(
                    ResultErr(
                        response.errorCode.toString(),
                        response.errorMsg
                    )
                )
            } else {
                successBlock?.let { it() }
                NetCallback.Success(response.data)
            }
        }
    }


}

