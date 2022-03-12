package com.example.trackerapp.data.repositories.walk

import android.util.Log
import com.example.trackerapp.data.dao.WalkDao
import com.example.trackerapp.entity.NewWalk
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.Walk
import com.example.trackerapp.utils.toWalkData
import com.example.trackerapp.utils.toWalkList
import javax.inject.Inject

class LocalWalkRepository @Inject constructor(
    private val walkDao: WalkDao,
) : WalkRepository {
    override suspend fun insertWalk(newWalk: NewWalk): Result<Unit> {
        return try {
            val insertWalkResult = walkDao.insertWalk(newWalk.toWalkData())

            if (insertWalkResult > 0) {
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
            Result.Success(
                walkDao
                    .getWalkList()
                    .toWalkList()
                    .reversed()
            )
        } catch (e: Exception) {
            Log.e("LocalWalkRepository", e.message ?: "Request Data Error")
            Result.Error(e)
        }

    override suspend fun deleteWalkList(idToDelete: Long): Result<Unit> {
        return try {
            val deleteWalkResult = walkDao.delete(idToDelete)
            if (deleteWalkResult > 0) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Delete Walk was failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}