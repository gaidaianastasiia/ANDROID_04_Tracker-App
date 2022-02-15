package com.example.trackerapp.domain

import com.example.trackerapp.data.repositories.UserLocationRepository
import javax.inject.Inject

class DeleteAllUserLocationsInteractor @Inject constructor(
    private val repository: UserLocationRepository,
    ) {
    suspend operator fun invoke() = repository.deleteAllUserLocations()
}