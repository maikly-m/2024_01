package com.example.u.net.api

import com.example.u.net.model.Banner
import com.example.u.net.model.BaseModel
import retrofit2.Call
import retrofit2.http.GET


interface RequestCenter {

    @GET("/banner/json")
    suspend fun getBanner(): BaseModel<MutableList<Banner>>

}