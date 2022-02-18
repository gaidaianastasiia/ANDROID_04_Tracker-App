package com.example.trackerapp.data.repositories.walk

import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.Walk

interface WalkRepository {
    suspend fun insertWalk(walk: Walk): Result<Unit>
    suspend fun getWalkList(): Result<List<Walk>>
}