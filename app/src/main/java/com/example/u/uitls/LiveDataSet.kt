package com.example.u.uitls

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.example.u.uitls.thread.AppExecutors

object LiveDataSet {
    fun <T> updateChangeValue(mld: MutableLiveData<T>?, d: T): Boolean {
        if (mld == null || d == mld.value) {
            return false
        }
        updateValue<T>(mld, d)
        return true
    }
    fun <T> setChangeValue(mld: MutableLiveData<T>?, d: T): Boolean {
        if (mld == null || d == mld.value) {
            return false
        }
        setValue<T>(mld, d)
        return true
    }
    private fun <T> updateValue(mld: MutableLiveData<T>?, d: T) {
        if (mld == null) {
            return
        }
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            mld.setValue(d)
        } else {
            mld.postValue(d)
        }
    }

    fun <T> setValue(mld: MutableLiveData<T>?, d: T) {
        if (mld == null) {
            return
        }
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            mld.setValue(d)
        } else {
            postSetValue<T>(mld, d)
        }
    }

    private fun <T> postSetValue(mld: MutableLiveData<T>, d: T) {
        AppExecutors.globalAppExecutors()?.mainThread()?.execute(
            SetValueRunnable.create(mld, d)
        )
    }

    private class SetValueRunnable<T> private constructor(
        private val liveData: MutableLiveData<T>,
        private val data: T
    ) :
        Runnable {
        override fun run() {
            liveData.value = data
        }

        companion object {
            fun <T> create(
                liveData: MutableLiveData<T>,
                data: T
            ): SetValueRunnable<T> {
                return SetValueRunnable<T>(liveData, data)
            }
        }
    }
}