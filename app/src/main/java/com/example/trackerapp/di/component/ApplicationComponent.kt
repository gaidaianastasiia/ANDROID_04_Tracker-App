package com.example.trackerapp.di.component

import com.example.trackerapp.Application
import com.example.trackerapp.di.module.ActivityModule
import com.example.trackerapp.di.module.ApplicationModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityModule::class,
    ApplicationModule::class
])
@Singleton
interface ApplicationComponent : AndroidInjector<Application> {
    @Component.Factory
    interface Factory : AndroidInjector.Factory<Application>
}