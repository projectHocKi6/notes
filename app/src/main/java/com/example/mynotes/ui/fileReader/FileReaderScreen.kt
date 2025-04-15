package com.example.mynotes.ui.fileReader

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.data.drawing.DrawingEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FileReaderScreen(
    viewModel: FileReaderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToDrawing: (Long?) -> Unit,
    navigateBack: () -> Unit
) {
    val drawings by viewModel.getAllDrawings().collectAsState(initial = emptyList())
    val loadStatus by viewModel.loadStatus.collectAsState()
    val selectedDrawing by viewModel.selectedDrawing.collectAsState()
    val loadedBitmap by viewModel.loadedBitmap.collectAsState()
    val context = LocalContext.current

    // Handle load status
    LaunchedEffect(loadStatus) {
        when (loadStatus) {
            is FileReaderViewModel.LoadStatus.Success -> {
                delay(Toast.LENGTH_SHORT.toLong())
                viewModel.resetLoadStatus()
            }
            is FileReaderViewModel.LoadStatus.Error -> {
                val errorMsg = (loadStatus as FileReaderViewModel.LoadStatus.Error).message
                Toast.makeText(context, "Lỗi tải file: $errorMsg", Toast.LENGTH_SHORT).show()
                delay(Toast.LENGTH_SHORT.toLong())
                viewModel.resetLoadStatus()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.systemBars.asPaddingValues() ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = navigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "")
            }

            Text(
                text = "Danh sách của bạn",
                style = MaterialTheme.typography.headlineMedium
            )

            // Empty box to balance layout
            Box(modifier = Modifier.size(48.dp))
        }

        // Show drawing preview or drawing list
        if (selectedDrawing != null && loadedBitmap != null) {
            // Drawing details and preview
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedDrawing!!.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Ngày tạo: ${formatDate(selectedDrawing!!.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Preview Image
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(loadedBitmap)
                            .build()
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Edit btn
                    Button(
                        onClick = { navigateToDrawing(selectedDrawing!!.id.toLong()) },
                        Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "")
                        Text("Chỉnh sửa")
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                    // Delete btn
                    Button(
                        onClick = {
                            viewModel.deleteDrawing(selectedDrawing!!.id.toLong())
                            viewModel.clearSelection()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier =  Modifier.weight(1f)

                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xóa")
                    }
                }

                // Back to list btn
                TextButton(
                    onClick = { viewModel.clearSelection() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Thoát")
                }
            }
        } else {
            // New drawing button
            Button(
                onClick = { navigateToDrawing(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tạo bản vẽ mới")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator or list
            if (loadStatus is FileReaderViewModel.LoadStatus.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (drawings.isEmpty()) {
                    // No drawings message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Danh sách trống!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // List of saved drawings
                    LazyColumn {
                        items(drawings) { drawing ->
                            DrawingItem(
                                drawing = drawing,
                                onClick = { viewModel.loadDrawingById(drawing.id.toLong()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawingItem(
    drawing: DrawingEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drawing.filePath)
                        .size(coil.size.Size(100, 100))
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Drawing details
            Column {
                Text(
                    text = drawing.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ngày tạo: ${formatDate(drawing.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Helper function to format date
private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}