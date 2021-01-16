package ru.shirobokov.reanimation

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.shirobokov.reanimation.di.diModules

class App : Application() {

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(diModules))
        }
    }
}