package com.example.trackerapp.di.module

import com.example.trackerapp.data.repositories.LocalUserLocationRepository
import com.example.trackerapp.data.repositories.UserLocationRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun bindUserLocationRepository(localUserLocationRepository: LocalUserLocationRepository): UserLocationRepository
}