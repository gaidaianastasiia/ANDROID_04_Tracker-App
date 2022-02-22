package com.example.trackerapp.data.repositories.user_location

import com.example.trackerapp.data.dao.UserLocationDao
import com.example.trackerapp.data.entity.UserLocationData
import kotlinx.coroutines.flow.Flow
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.utils.toUserLocationData
import java.time.LocalDateTime
import javax.inject.Inject

class LocalUserLocationRepository @Inject constructor(
    private val userLocationDao: UserLocationDao,
) : UserLocationRepository {
    override suspend fun insertUserLocation(userLocation: UserLocation): Result<Unit> {
        return try {
            val id = userLocationDao.insertUserLocation(userLocation.toUserLocationData())
            if (id > 0) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Insert was failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllUserLocations(): Flow<List<UserLocationData>> =
        userLocationDao.getAllUserLocations()

    override suspend fun getLastUserLocation(): UserLocationData? =
        userLocationDao.getLastUserLocation()

    override fun getCoveredDistance(): Flow<Double?> =
        userLocationDao.getCoveredDistance()

    override suspend fun getFinalDistance(): Double? =
        userLocationDao.getFinalDistance()

    override suspend fun getStartTime(): LocalDateTime? =
        userLocationDao.getStartTime()

    override suspend fun deleteAllUserLocations() {
        userLocationDao.deleteAllUserLocations()
    }
}

