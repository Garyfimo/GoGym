package com.garyfimo.gogym

import android.app.Application
import com.garyfimo.gogym.config.AppConfig
import com.garyfimo.gogym.di.initKoin
import org.koin.android.ext.koin.androidContext

class GoGymApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val appConfig = AppConfig(
            baseUrl = BuildConfig.BASE_URL,
            environment = BuildConfig.ENV_NAME
        )
        
        initKoin(appConfig) {
            androidContext(this@GoGymApplication)
        }
    }
}
