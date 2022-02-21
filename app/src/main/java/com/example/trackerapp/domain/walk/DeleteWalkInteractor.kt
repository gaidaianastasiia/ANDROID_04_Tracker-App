package com.example.trackerapp.domain.walk

import com.example.trackerapp.data.repositories.walk.WalkRepository
import com.example.trackerapp.entity.Result
import javax.inject.Inject

class DeleteWalkInteractor @Inject constructor(
    private val repository: WalkRepository,
) {
    suspend operator fun invoke(idToDelete: Long): Result<Unit> =
        repository.deleteWalkList(idToDelete)
}