package com.example.u.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.u.uitls.LiveDataSet
import com.example.u.uitls.thread.SingleLiveEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PermissionViewModel : ViewModel() {

    private val _text = MutableLiveData("_text")
    val text: LiveData<String> = _text

    private val _text2 = MutableSharedFlow<String>()
    val text2: LiveData<String> = _text2.asLiveData()

    private val _text3 = MutableStateFlow("_text3")
    val text3: LiveData<String> = _text3.asLiveData()

    // 使用single
    private val _text4 = SingleLiveEvent<String>()
    val text4: LiveData<String> = _text4


    fun testFlow(){
        viewModelScope.launch {
            _text2.emit("_text2 testFlow")
            _text3.emit("_text3 testFlow")
            LiveDataSet.setValue(_text, "_text")
            LiveDataSet.setValue(_text4, "_text4")
        }
    }
}