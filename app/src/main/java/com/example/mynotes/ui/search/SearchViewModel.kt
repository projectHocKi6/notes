package com.example.mynotes.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.NotesRepository
import com.example.mynotes.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    var noteUiState by mutableStateOf("")
        private set
    private val _searchResult = MutableStateFlow(HomeUiState())
    var searchResult: StateFlow<HomeUiState> by mutableStateOf(_searchResult)
    fun updateUiState(title: String) {
        noteUiState = title
    }

    suspend fun searchNotes(title: String) {
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                notesRepository.getNotesByTitle(title)
                    .collect{notes ->
                        _searchResult.value.noteList.value = notes
                    }
            }
        }
    }
}