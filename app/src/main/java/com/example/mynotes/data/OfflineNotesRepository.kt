package com.example.mynotes.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

    class OfflineNotesRepository(private val noteDao: NoteDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun getAllFavouriteNotes(): Flow<List<Note>> = noteDao.getAllFavouriteNotes()

    override fun getAllSecureNotes(): Flow<List<Note>> = noteDao.getAllSecureNotes()

    override fun getNotesStream(id: Int): Flow<Note?> = noteDao.getNote(id)

    override fun getNotesByTitle(title: String): Flow<List<Note>> = noteDao.getNotesbyTitle(title)

    override suspend fun insertNote(note: Note) {
        noteDao.insert(note)
    }
    suspend fun insertOrUpdate(note: Note) {
        val existingNote = noteDao.getNote(note.id).firstOrNull()
        if (existingNote == null) {
            noteDao.insert(note) // Nếu chưa có thì thêm mới
        } else {
            noteDao.update(note) // Nếu đã có thì cập nhật
        }
    }


    override suspend fun deleteNote(note: Note) = noteDao.delete(note)

    override suspend fun deleteNoteByListId(ids: List<Int>) = noteDao.deleteByListId(ids)

    override suspend fun updateNote(note: Note) = noteDao.update(note)
        override suspend fun dataSync() {
        }

    }
