package com.thunder.thunderplane.base

import android.app.Application
import com.thunder.thunderplane.module.appModule
import org.koin.core.context.startKoin

class MyApplication : Application() {

    companion object{
        lateinit var instance: Application
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            modules(appModule)
        }
    }


}