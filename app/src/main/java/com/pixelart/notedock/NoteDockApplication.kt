package com.pixelart.notedock

import android.app.Application
import com.pixelart.notedock.di.viewModelModule
import com.pixelart.notedock.di.firebaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NoteDockApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //Start Koin
        startKoin {
            androidLogger()
            androidContext(this@NoteDockApplication)
            modules(viewModelModule, firebaseModule)
        }
    }
}