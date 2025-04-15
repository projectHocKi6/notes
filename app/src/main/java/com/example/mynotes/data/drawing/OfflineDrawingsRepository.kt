package com.example.mynotes.data.drawing

import kotlinx.coroutines.flow.Flow



// Your current DrawingsRepository should be converted to an interface (as shown above)
// Then implement the interface in your OfflineDrawingsRepository class:

class OfflineDrawingsRepository(private val drawingsDao: DrawingsDao) : DrawingsRepository {

    override fun getAllDrawings(): Flow<List<DrawingEntity>> {
        return drawingsDao.getAllDrawings()
    }

    override suspend fun insertDrawing(drawing: DrawingEntity): Long {
        return drawingsDao.insert(drawing)
    }

    override suspend fun updateDrawing(drawing: DrawingEntity) {
        drawingsDao.update(drawing)
    }

    override suspend fun deleteDrawing(drawing: Long) {
        drawingsDao.delete(drawing)
    }

    override suspend fun getDrawingById(id: Long): DrawingEntity? {
        return drawingsDao.getDrawingById(id)
    }
}