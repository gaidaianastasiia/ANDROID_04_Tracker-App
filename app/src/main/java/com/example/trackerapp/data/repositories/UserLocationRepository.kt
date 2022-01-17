package com.example.trackerapp.data.repositories

import com.example.trackerapp.data.entity.UserLocationData
import kotlinx.coroutines.flow.Flow
import com.example.trackerapp.entity.Result
import java.time.LocalDateTime

interface UserLocationRepository {
    suspend fun insertUserLocation(
        latitude: Double,
        longitude: Double,
        distance: Double,
        time: LocalDateTime,
        speed: Double,
    ): Result<Unit>

    fun getAllUserLocations(): Flow<List<UserLocationData>>
    suspend fun getLastUserLocation(): UserLocationData?
    fun getCoveredDistance(): Flow<Double?>
    suspend fun deleteAllUserLocations()
}