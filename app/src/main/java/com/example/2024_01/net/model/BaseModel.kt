package com.example.`2024_01`.net.model

data class BaseModel<out T>(val errorCode: Int, val errorMsg: String, val data: T)