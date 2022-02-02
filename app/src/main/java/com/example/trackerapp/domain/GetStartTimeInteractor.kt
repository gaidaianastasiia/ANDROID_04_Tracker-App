package com.example.trackerapp.domain

import com.example.trackerapp.data.repositories.UserLocationRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetStartTimeInteractor @Inject constructor(
    private val repository: UserLocationRepository
) {
    suspend operator fun invoke(): LocalDateTime? = repository.getStartTime()
}