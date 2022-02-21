package com.example.trackerapp.data.repositories.walk

import com.example.trackerapp.entity.NewWalk
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.Walk

interface WalkRepository {
    suspend fun insertWalk(newWalk: NewWalk): Result<Unit>
    suspend fun getWalkList(): Result<List<Walk>>
    suspend fun deleteWalkList(idToDelete: Long): Result<Unit>
}