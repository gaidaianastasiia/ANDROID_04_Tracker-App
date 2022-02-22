package com.example.trackerapp.di.module

import com.example.trackerapp.data.repositories.user_location.LocalUserLocationRepository
import com.example.trackerapp.data.repositories.walk.LocalWalkRepository
import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import com.example.trackerapp.data.repositories.walk.WalkRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun bindUserLocationRepository(localUserLocationRepository: LocalUserLocationRepository): UserLocationRepository

    @Binds
    fun bindWalkRepository(localWalkRepository: LocalWalkRepository): WalkRepository
}