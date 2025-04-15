package com.example.mynotes.data.drawing

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingsDao {
    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): Flow<List<DrawingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drawing: DrawingEntity): Long

    @Update
    suspend fun update(drawing: DrawingEntity)

    @Query("DELETE FROM drawings WHERE id = :drawingId")
    suspend fun delete(drawingId: Long)

    @Query("SELECT * FROM drawings WHERE id = :id")
    suspend fun getDrawingById(id: Long): DrawingEntity?
}