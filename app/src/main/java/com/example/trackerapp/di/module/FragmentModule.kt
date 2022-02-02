package com.example.trackerapp.di.module

import com.example.trackerapp.di.scope.FragmentScope
import com.example.trackerapp.presentation.fragment.tracker.TrackerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTrackerFragment(): TrackerFragment
}