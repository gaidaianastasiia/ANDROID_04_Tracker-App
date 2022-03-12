package com.example.trackerapp.domain.tracker.tracker_data

import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.utils.toUserLocation
import javax.inject.Inject

class GetLastUserLocationInteractor @Inject constructor(
    private val repository: UserLocationRepository,
) {
    suspend operator fun invoke(): UserLocation? = repository.getLastUserLocation()?.toUserLocation()
}
