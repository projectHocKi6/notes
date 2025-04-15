package com.example.mynotes.ui.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.CoverTopAppBar
import com.example.mynotes.R
import com.example.mynotes.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object NoteCoverDestination : NavigationDestination {
    override val route = "CoverScreen"
    override val titleRes: Int
        get() = TODO("Not yet implemented")
    const val noteIdArg = "noteId"
}


@Composable
fun CoverScreen(
    viewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { CoverTopAppBar(title = "Bìa", onNavBack = navigateBack) }
    ) { contentPadding ->
        Surface(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(contentPadding)

        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            ) {
                CoverPickerScreen(viewModel)
            }
        }
    }
}

@Composable
fun CoverPickerScreen(viewModel: EditViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SwitchBar(viewModel)
            SelectedCover(viewModel)
        }
        Select(
            viewModel,
            Modifier
                .height(180.dp)
                .padding(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (viewModel.noteUiState.noteDetail.coverOn) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .align(Alignment.BottomStart)
        )
    }
}


@Composable
fun Select(viewModel: EditViewModel, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val listCover = listOf(
        "cover_1" to R.drawable.cover_1,
        "cover_2" to R.drawable.cover_2,
        "cover_3" to R.drawable.cover_3,
        "cover_4" to R.drawable.cover_4,
        "cover_5" to R.drawable.cover_5,
        "cover_6" to R.drawable.cover_6,
        "cover_7" to R.drawable.cover_7,
        "cover_8" to R.drawable.cover_8,
        "cover_9" to R.drawable.cover_9,

    )

    Box(modifier = modifier) {
        Column(Modifier.padding(5.dp)) {
            Text(text = "Kiểu bìa")
            Spacer(Modifier.weight(1f))
            LazyRow {
                items(listCover) { (coverName, coverRes) ->
                    Image(
                        painter = painterResource(coverRes),
                        contentDescription = "Cover Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = {
                                viewModel.updateUiState(
                                    viewModel.noteUiState.noteDetail.copy(
                                        cover = coverName
                                    )
                                )
                                coroutineScope.launch {
                                    viewModel.saveNote()
                                }
                            })
                    )
                }
            }
        }
    }
}


@Composable
fun SelectedCover(viewModel: EditViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coverName = viewModel.noteUiState.noteDetail.cover

    val coverResId = if (coverName.isNotEmpty()) {
        context.resources.getIdentifier(coverName, "drawable", context.packageName)
    } else {
        R.drawable.cover_1
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 50.dp)
    ) {
        Image(
            painter = painterResource(id = if (coverResId != 0) coverResId else R.drawable.cover_1),
            modifier = Modifier.size(180.dp),
            contentDescription = "current cover",
            colorFilter = if (viewModel.noteUiState.noteDetail.coverOn) null else ColorFilter.colorMatrix(ColorMatrix().apply {
                setToSaturation(0f)
            })
        )
        Spacer(Modifier.padding(14.dp))
        Text(
            text = viewModel.noteUiState.noteDetail.title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun SwitchBar(viewModel: EditViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.padding(start = 30.dp, end = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (viewModel.noteUiState.noteDetail.coverOn) "Bật" else "Tắt",
            color = if (viewModel.noteUiState.noteDetail.coverOn) colorResource(R.color.radioBtn) else MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.weight(1f))
        Switch(
            checked = viewModel.noteUiState.noteDetail.coverOn,
            onCheckedChange = {
                viewModel.updateUiState(viewModel.noteUiState.noteDetail.copy(coverOn = it))
                coroutineScope.launch {
                    viewModel.saveNote()
                }
            },
            thumbContent = {
                if (viewModel.noteUiState.noteDetail.coverOn) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )


    }
}
