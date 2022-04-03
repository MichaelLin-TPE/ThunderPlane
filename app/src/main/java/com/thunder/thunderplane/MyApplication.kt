package com.thunder.thunderplane

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object{
        lateinit var instance: Application
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}