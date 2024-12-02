package com.example.u.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Permission Fragment"
    }
    val text: LiveData<String> = _text
}