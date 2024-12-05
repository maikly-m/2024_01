package com.example.u.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.u.net.api.RequestCenter
import com.example.u.net.awaitCall
import com.example.u.net.model.Banner
import com.example.u.net.model.NetCallback
import com.example.u.net.net.BaseRepository
import com.example.u.net.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class NetTestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    // flow
    private val _textFlow = MutableStateFlow("")
    val textFlow: LiveData<String> = _textFlow.asLiveData()

    fun getBanner(){
        viewModelScope.launch {
            val banner = homeRepository.getBanner()
            if (banner is NetCallback.Success) {
                _textFlow.emit("getTextFlow ok")
                Timber.e("getTextFlow: ok -- $banner")
            } else if (banner is NetCallback.Error) {
                Timber.e("getTextFlow: fail -- "+banner.exception.msg )
                _textFlow.emit("getTextFlow fail")
            }
        }
    }

    private val homeRepository = NetTestRepository(RetrofitClient.instance)
}
class  NetTestRepository(private val service: RetrofitClient) : BaseRepository() {
    private val TAG = "NetTestRepository"

    suspend fun getBanner(): NetCallback<List<Banner>> {

        return callRequest(call = {
            //requestBanner()
            requestBanner2()
        })
    }

    private suspend fun requestBanner() =
        handleResponse(service.create(RequestCenter::class.java).getBanner())

    private suspend fun requestBanner2(): NetCallback<List<Banner>> {
        return handleResponse(service.create(RequestCenter::class.java).getBanner2().awaitCall())
    }

}