package com.example.trackerapp.domain.tracker.tracker_data

import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetStartTimeInteractor @Inject constructor(
    private val repository: UserLocationRepository
) {
    suspend operator fun invoke(): LocalDateTime? = repository.getStartTime()
}