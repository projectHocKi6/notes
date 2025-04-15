package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE from notes WHERE id IN (:ids)")
    fun deleteByListId(ids: List<Int>)

    @Query("SELECT * from notes WHERE id = :id ")
    fun getNote(id: Int): Flow<Note>

    @Query("SELECT * from notes WHERE status != -1 ORDER BY id ASC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * from notes WHERE status = 1 ORDER BY id ASC")
    fun getAllFavouriteNotes(): Flow<List<Note>>

    @Query("SELECT * from notes WHERE status = -1 ORDER BY id ASC")
    fun getAllSecureNotes(): Flow<List<Note>>

    @Query("SELECT * from notes WHERE  status != -1 and title LIKE '%'||  :title|| '%' ")
    fun getNotesbyTitle(title: String): Flow<List<Note>>

}