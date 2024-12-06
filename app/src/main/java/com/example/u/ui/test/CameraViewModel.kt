package com.example.u.ui.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.u.net.api.RequestCenter
import com.example.u.net.model.Banner
import com.example.u.net.model.NetCallback
import com.example.u.net.net.BaseRepository
import com.example.u.net.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val TAG = "DashboardViewModel"
    // livedata
    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
    fun setText(value: String){
        _text.postValue(value)
    }

    // flow
    private val _textFlow = MutableStateFlow("")
    val textFlow: LiveData<String> = _textFlow.asLiveData()
    fun getTextFlow(){
        viewModelScope.launch {
            val banner = homeRepository.getBanner()
            if (banner is NetCallback.Success) {
                _textFlow.emit("getTextFlow ok")
                Log.e(TAG, "getTextFlow: ok -- $banner")
            } else if (banner is NetCallback.Error) {
                Log.e(TAG, "getTextFlow: fail -- "+banner.exception.msg )
                _textFlow.emit("getTextFlow fail")
            }
        }
    }

    private val homeRepository = CameraRepository(RetrofitClient.instance)

}

class CameraRepository(private val service: RetrofitClient) : BaseRepository() {
    private val TAG = "CameraRepository"

    suspend fun getBanner(): NetCallback<List<Banner>> {

        return callRequest(call = { requestBanner() })
    }

    private suspend fun requestBanner() =
        handleResponse(service.create(RequestCenter::class.java).getBanner())
}