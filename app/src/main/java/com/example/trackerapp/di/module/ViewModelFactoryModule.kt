package com.example.trackerapp.di.module

import com.example.trackerapp.presentation.activity.MainViewModel
import com.example.trackerapp.presentation.base.ViewModelAssistedFactory
import com.example.trackerapp.presentation.fragment.tracker.TrackerViewModel
import com.example.trackerapp.presentation.fragment.walk_list.WalkListViewModel
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {
    // >> Activities

    @Binds
    fun bindMainViewModelAssistedFactory(
        viewModelFactory: MainViewModel.Factory,
    ): ViewModelAssistedFactory<MainViewModel>

    // >> Activities

    // >> Fragments

    @Binds
    fun bindTrackerViewModelAssistedFactory(
        viewModelFactory: TrackerViewModel.Factory
    ) : ViewModelAssistedFactory<TrackerViewModel>

    @Binds
    fun bindWalkListViewModelAssistedFactory(
        viewModelFactory: WalkListViewModel.Factory
    ) : ViewModelAssistedFactory<WalkListViewModel>

    // >> Fragments
}
