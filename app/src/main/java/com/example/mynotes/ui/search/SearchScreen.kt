package com.example.mynotes.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.ExpandTopAppBar
import com.example.mynotes.SearchTopAppBar
import com.example.mynotes.ui.home.HomeUiState
import com.example.mynotes.ui.home.NotesCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    navigateToEdit: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState = viewModel.noteUiState
    Scaffold(
        topBar = {
            Column {
                ExpandTopAppBar(scrollBehavior)
                SearchTopAppBar(
                    navigateBack,
                    uiState,
                    viewModel::updateUiState,
                )
            }

        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        ListResult(contentPadding, uiState, viewModel,navigateToEdit )
    }
}
@Composable
fun ListResult(
    contentPadding: PaddingValues,
    uiState: String,
    viewModel: SearchViewModel,
    navigateToEdit: (Int) -> Unit,
) {
    val seResult by viewModel.searchResult.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.isNotEmpty()) {
            viewModel.searchNotes(uiState)
        }
    }

    if (uiState.isNotEmpty()) {
        if (seResult.noteList.value.isNotEmpty()) {

            ListNote(
                seResult,
                contentPadding,
                navigateToEdit
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column(Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Chú thích không tồn tại!",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 26.sp
                    )
                }

            }

        }
    } else {
    }
}



@Composable
fun ListNote(
    searchResult: HomeUiState,
    contentPadding: PaddingValues,
    navigateToEdit: (Int) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(135.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .fillMaxSize()
    ) {

        items(searchResult.noteList.value, key = { it.id }) { note ->
            NotesCard(
                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onItemClick = {navigateToEdit(note.id) },
                note = note,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    //rộng : dài
                    .aspectRatio(0.7f)
            )
        }
    }

}