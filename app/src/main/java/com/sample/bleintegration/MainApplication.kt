package com.sample.bleintegration

import android.app.Application
import com.mx.mxSdk.ConnectManager

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ConnectManager.share().init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ConnectManager.share().release()
    }
}