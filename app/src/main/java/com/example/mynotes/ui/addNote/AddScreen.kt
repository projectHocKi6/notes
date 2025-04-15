package com.example.mynotes.ui.addNote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AddTopAppBar
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.ui.edit.TextInp
import kotlinx.coroutines.launch

@Composable
fun AddScreen(
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    fontSize:TextUnit,
) {
    val addUiState = viewModel.noteUiState
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            AddTopAppBar({ navigateBack() }, addUiState, viewModel::updateUiState)
        }
    ) { contentPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {

            Column {
                TextInp(modifier = Modifier.fillMaxWidth(),
                    noteUiState = addUiState,
                    onValueChange = viewModel::updateUiState,
                    onSave = {
                        coroutineScope.launch {
                            viewModel.saveNote()
                            navigateBack()
                        }
                    },
                    fontSize = fontSize
                )
            }
        }
    }
}
