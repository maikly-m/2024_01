package com.example.u.net.model

data class BaseModel<out T>(val errorCode: Int, val errorMsg: String, val data: T)