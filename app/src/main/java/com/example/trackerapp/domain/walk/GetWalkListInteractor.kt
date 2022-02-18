package com.example.trackerapp.domain.walk

import com.example.trackerapp.data.repositories.walk.WalkRepository
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.Walk
import javax.inject.Inject

class GetWalkListInteractor @Inject constructor(
    private val repository: WalkRepository,
) {
    suspend operator fun invoke(): Result<List<Walk>> = repository.getWalkList()
}