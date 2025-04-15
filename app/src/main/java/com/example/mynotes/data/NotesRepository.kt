package com.example.mynotes.data


import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesStream(): Flow<List<Note>>
    fun getAllFavouriteNotes(): Flow<List<Note>>
    fun getAllSecureNotes(): Flow<List<Note>>
    fun getNotesStream(id: Int): Flow<Note?>
    fun getNotesByTitle(title: String): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteNoteByListId(ids: List<Int>)
    suspend fun updateNote(note: Note)
    suspend fun dataSync()
}