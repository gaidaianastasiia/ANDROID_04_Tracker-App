package com.example.trackerapp.data.repositories.walk

import android.util.Log
import com.example.trackerapp.data.dao.WalkDao
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.Walk
import com.example.trackerapp.utils.toWalkData
import com.example.trackerapp.utils.toWalkList
import javax.inject.Inject

class LocalWalkRepository @Inject constructor(
    private val walkDao: WalkDao
): WalkRepository {
    override suspend fun insertWalk(walk: Walk): Result<Unit> {
        return try {
            val id = walkDao.insertWalk(walk.toWalkData())
            if (id > 0) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Insert was failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getWalkList(): Result<List<Walk>> =
        try {
            Result.Success(walkDao.getWalkList().toWalkList())
        } catch (e: Exception) {
            Log.e("LocalWalkRepository", e.message ?: "Request Data Error")
            Result.Error(e)
        }
}