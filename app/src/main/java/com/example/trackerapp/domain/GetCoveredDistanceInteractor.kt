package com.example.trackerapp.domain

import com.example.trackerapp.data.repositories.UserLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

class GetCoveredDistanceInteractor @Inject constructor(
    private val repository: UserLocationRepository,
) {
    operator fun invoke(): Flow<Double> =
        repository.getCoveredDistance()
            .map { it ?: 0.0 }
            .map { (it * 10.0).roundToInt() / 10.0 }

}