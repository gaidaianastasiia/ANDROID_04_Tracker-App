package com.example.trackerapp.data.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trackerapp.data.entity.WalkData

@Dao
interface WalkDao {
    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertWalk(walk: WalkData): Long

    @Query("SELECT * FROM WalkData")
    @Throws(SQLiteException::class)
    suspend fun getWalkList(): List<WalkData>
}