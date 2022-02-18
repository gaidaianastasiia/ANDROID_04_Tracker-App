package com.example.trackerapp.domain.tracker

import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import javax.inject.Inject

class DeleteAllUserLocationsInteractor @Inject constructor(
    private val repository: UserLocationRepository,
    ) {
    suspend operator fun invoke() = repository.deleteAllUserLocations()
}