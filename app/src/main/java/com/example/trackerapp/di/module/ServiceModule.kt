package com.example.trackerapp.di.module

import com.example.trackerapp.di.scope.ServiceScope
import com.example.trackerapp.services.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {
    @ServiceScope
    @ContributesAndroidInjector
    abstract fun contributeService(): LocationService
}