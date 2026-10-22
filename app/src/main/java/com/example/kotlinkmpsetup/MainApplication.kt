package com.example.kotlinkmpsetup

import android.app.Application
import com.example.kotlinkmpsetup.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * ====================================================================================
 * APPLICATION CLASS: KOIN DI INITIALIZATION
 * ====================================================================================
 * Configures Koin dependency injection graph on Application start.
 *
 * INTERVIEW POINT:
 * `startKoin` initializes Koin at app launch. Passing `androidContext` enables Koin
 * to inject `Context` into dependencies (like `TokenDataStoreManager` and `NetworkConnectivityObserver`).
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApplication)
            modules(appModules)
        }
    }
}
