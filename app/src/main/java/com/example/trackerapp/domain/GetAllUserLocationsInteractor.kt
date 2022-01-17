package com.example.trackerapp.domain

import com.example.trackerapp.data.repositories.UserLocationRepository
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.utils.toUserLocationList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllUserLocationsInteractor @Inject constructor(
    private val repository: UserLocationRepository
) {
    operator fun invoke(): Flow<List<UserLocation>> =
        repository.getAllUserLocations().map { list -> list.toUserLocationList() }
}