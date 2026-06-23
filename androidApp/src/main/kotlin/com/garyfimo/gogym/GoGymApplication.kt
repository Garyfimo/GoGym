package com.garyfimo.gogym

import android.app.Application
import com.garyfimo.gogym.di.initKoin
import org.koin.android.ext.koin.androidContext

class GoGymApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GoGymApplication)
        }
    }
}
