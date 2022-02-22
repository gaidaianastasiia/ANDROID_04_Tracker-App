package com.example.trackerapp.di.module

import com.example.trackerapp.di.scope.FragmentScope
import com.example.trackerapp.presentation.fragment.tracker.TrackerFragment
import com.example.trackerapp.presentation.fragment.walk_list.WalkListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTrackerFragment(): TrackerFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeWalkListFragment(): WalkListFragment
}