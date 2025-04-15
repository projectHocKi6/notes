package com.example.mynotes.data

import android.content.Context
import com.example.mynotes.data.drawing.DrawingsRepository
import com.example.mynotes.data.drawing.OfflineDrawingsRepository

interface AppContainer{
    val notesRepository: NotesRepository
    val notifyRepository: NotifyRepository
    val drawingsRepository: DrawingsRepository
}

class AppDataContainer(private val context: Context): AppContainer{
    private val offlineRepo = OfflineNotesRepository(NoteDatabase.getDatabase(context).noteDao())
    private val cloudRepo = CloudNotesRepository()

    override val notesRepository: NotesRepository by lazy {
        HybridNotesRepository(offlineRepo, cloudRepo)
    }

    override val notifyRepository = WorkManagerNoteRepository(context)

    // Properly implement the drawings repository
    override val drawingsRepository: DrawingsRepository by lazy {
        OfflineDrawingsRepository(NoteDatabase.getDatabase(context).drawingsDao())
    }
}
