package com.example.u

import android.app.Application
import timber.log.Timber

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) // Plant DebugTree for debug builds
        } else {
            // Plant a different tree for release builds (e.g., Crashlytics, Firebase)
            // Timber.plant(MyReleaseTree())
        }
    }
}