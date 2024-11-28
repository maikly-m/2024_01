package com.example.`2024_01`.ui.dashboard

import com.example.`2024_01`.net.api.RequestCenter
import com.example.`2024_01`.net.model.Banner
import com.example.`2024_01`.net.model.NetCallback
import com.example.`2024_01`.net.net.BaseRepository
import com.example.`2024_01`.net.net.RetrofitClient

class DashboardRepository(private val service: RetrofitClient) : BaseRepository() {
    private val TAG = "DashboardRepository"

    suspend fun getBanner(): NetCallback<List<Banner>> {

        return callRequest(call = { requestBanner() })
    }

    private suspend fun requestBanner() =
        handleResponse(service.create(RequestCenter::class.java).getBanner())



}


