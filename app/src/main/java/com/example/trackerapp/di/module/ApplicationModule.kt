package com.example.trackerapp.di.module

import android.content.Context
import com.example.trackerapp.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {
    @Provides
    @Singleton
    @JvmStatic
    fun context(app: Application): Context {
        return app.applicationContext
    }
}