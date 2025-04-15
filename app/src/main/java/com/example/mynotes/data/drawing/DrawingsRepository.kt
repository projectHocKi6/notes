package com.example.mynotes.data.drawing

import kotlinx.coroutines.flow.Flow

interface DrawingsRepository {
    fun getAllDrawings(): Flow<List<DrawingEntity>>
    suspend fun insertDrawing(drawing: DrawingEntity): Long
    suspend fun updateDrawing(drawing: DrawingEntity)
    suspend fun deleteDrawing(drawing: Long)
    suspend fun getDrawingById(id: Long): DrawingEntity?
}