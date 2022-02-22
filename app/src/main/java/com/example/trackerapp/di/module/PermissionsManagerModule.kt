package com.example.trackerapp.di.module

import com.example.trackerapp.utils.PermissionsManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PermissionsManagerModule {
    @Provides
    @Singleton
    fun providePermissionsManager(): PermissionsManager = PermissionsManager()
}