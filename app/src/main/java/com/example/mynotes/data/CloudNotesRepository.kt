package com.example.mynotes.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CloudNotesRepository : NotesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("Note")

    override fun getAllNotesStream(): Flow<List<Note>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("CloudNotesRepo", "User not logged in!")
            close(Exception("User not logged in"))
            return@callbackFlow
        }

        val listener = notesCollection.whereEqualTo("userUID", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CloudNotesRepo", "Firestore error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
                    Log.d("CloudNotesRepo", "Fetched ${notes.size} notes for user $userId")
                    trySend(notes)
                }
            }
        awaitClose { listener.remove() }
    }


    override fun getAllFavouriteNotes(): Flow<List<Note>> = flow {
        val snapshot = notesCollection.whereEqualTo("status", 1).get().await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
        emit(notes)
    }

    override fun getAllSecureNotes(): Flow<List<Note>> = flow {
        val snapshot = notesCollection.whereEqualTo("status", -1).get().await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
        emit(notes)
    }

    override fun getNotesStream(id: Int): Flow<Note?> = flow {
        val snapshot = notesCollection.document(id.toString()).get().await()
        emit(snapshot.toObject(Note::class.java))
    }

    override fun getNotesByTitle(title: String): Flow<List<Note>> = flow {
        val snapshot = notesCollection
            .whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")
            .get().await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
        emit(notes)
    }

    override suspend fun insertNote(note: Note) {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("CloudNotesRepo", "Cannot insert note: User not logged in!")
            return
        }
        val noteWithUser = note.copy(userUID = userId)
        notesCollection.document(noteWithUser.id.toString()).set(noteWithUser).await()
    }


    override suspend fun deleteNote(note: Note) {
        val userId = getCurrentUserId()
        if (userId == null || userId != note.userUID) {
            return
        }
        notesCollection.document(note.id.toString()).delete().await()
    }

    override suspend fun deleteNoteByListId(ids: List<Int>) {
        val userId = getCurrentUserId()
        if (userId == null) {
            return
        }
        ids.forEach { id ->
            val currentNote = getNotesStream(id)
            if (userId != currentNote.first()?.userUID) return
            notesCollection.document(id.toString()).delete().await()
        }
    }

    override suspend fun updateNote(note: Note) {
        val cloudNote = getNotesStream(note.id)
            .firstOrNull()
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("CloudNotesRepo", "User not logged in! Cannot update note.")
            return
        }
        if (cloudNote == null) {
            insertNote(note)
        } else {
            val cloudUserUID = cloudNote.userUID ?: ""
            // Nếu note chưa có userUID, hoặc userUID trùng với tài khoản hiện tại thì cập nhật
            if (note.userUID.isEmpty() || cloudUserUID == userId) {
                val noteWithUser = note.copy(userUID = userId)
                notesCollection.document(noteWithUser.id.toString()).set(noteWithUser).await()
                Log.d("CloudNotesRepo", "Updated note ${note.id} for user $userId")
            } else {
                Log.w(
                    "CloudNotesRepo",
                    "User $userId does not have permission to update this note."
                )
            }
        }

    }


    override suspend fun dataSync() {
    }

    suspend fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}