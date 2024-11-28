package com.example.`2024_01`.net.api

import com.example.`2024_01`.net.model.Banner
import com.example.`2024_01`.net.model.BaseModel
import retrofit2.Call
import retrofit2.http.GET


interface RequestCenter {

    @GET("/banner/json")
    suspend fun getBanner(): BaseModel<MutableList<Banner>>

    @GET("/banner/json")
    fun getBannerCall(): Call<BaseModel<MutableList<Banner>>>

}