package com.example.trackerapp.di.component

import com.example.trackerapp.Application
import com.example.trackerapp.di.module.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class,
    ActivityModule::class,
    FragmentModule::class,
    ServiceModule::class,
    ViewModelFactoryModule::class,
    StorageModule::class,
    RepositoryModule::class
])
@Singleton
interface ApplicationComponent : AndroidInjector<Application> {
    @Component.Factory
    interface Factory : AndroidInjector.Factory<Application>
}