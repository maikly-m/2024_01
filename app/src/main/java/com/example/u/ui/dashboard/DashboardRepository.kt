package com.example.u.ui.dashboard

import com.example.u.net.api.RequestCenter
import com.example.u.net.model.Banner
import com.example.u.net.model.NetCallback
import com.example.u.net.net.BaseRepository
import com.example.u.net.net.RetrofitClient

class DashboardRepository(private val service: RetrofitClient) : BaseRepository() {
    private val TAG = "DashboardRepository"

    suspend fun getBanner(): NetCallback<List<Banner>> {

        return callRequest(call = { requestBanner() })
    }

    private suspend fun requestBanner() =
        handleResponse(service.create(RequestCenter::class.java).getBanner())



}


