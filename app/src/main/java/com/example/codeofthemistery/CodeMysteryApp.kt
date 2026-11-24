package com.example.codeofthemistery

import android.app.Application
import com.example.codeofthemistery.core.di.dataModule
import com.example.codeofthemistery.core.di.domainModule
import com.example.codeofthemistery.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CodeMysteryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CodeMysteryApp)
            modules(dataModule, domainModule, viewModelModule)
        }
    }
}
