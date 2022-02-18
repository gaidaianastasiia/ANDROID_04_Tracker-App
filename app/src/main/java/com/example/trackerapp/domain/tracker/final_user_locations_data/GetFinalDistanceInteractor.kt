package com.example.trackerapp.domain.tracker.final_user_locations_data

import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import com.example.trackerapp.utils.DEFAULT_DISTANCE
import javax.inject.Inject
import kotlin.math.roundToInt

class GetFinalDistanceInteractor @Inject constructor(
    private val repository: UserLocationRepository,
) {
    suspend operator fun invoke(): Double {
        val finalDistance = repository.getFinalDistance()

        return if (finalDistance == null) {
            DEFAULT_DISTANCE
        } else {
            (finalDistance * 10.0).roundToInt() / 10.0
        }
    }
}