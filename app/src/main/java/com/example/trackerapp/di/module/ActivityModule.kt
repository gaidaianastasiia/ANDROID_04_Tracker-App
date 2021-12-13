package com.example.trackerapp.di.module

import com.example.trackerapp.presentation.activity.MainActivity
import com.example.trackerapp.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): MainActivity
}