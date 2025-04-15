package com.example.mynotes.ui.draw

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    drawingId: Long? = null,
    navigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val saveStatus by viewModel.saveStatus.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(drawingId) {
        if (drawingId != null) {

            viewModel.loadDrawing(drawingId)
        } else {
            viewModel.clearDrawing()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        // Top toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = navigateBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text("Bảng vẽ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, fontSize = 30.sp)
            Row {
                IconButton(onClick = { viewModel.clearDrawing() }) {
                    Icon(Icons.Default.Delete, "Clear Drawing")
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            val bitmap = viewModel.getDrawingAsBitmap(
                                canvasSize.width,
                                canvasSize.height
                            )
                            viewModel.saveDrawing( bitmap = bitmap)
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, "Save Drawing")
                }
            }
        }
        LaunchedEffect(saveStatus) {
            when(saveStatus){
                is DrawingViewModel.SaveStatus.Success->{
                        Toast.makeText(context,"Lưu file thành công!", Toast.LENGTH_SHORT).show()
                }
                is DrawingViewModel.SaveStatus.Error->{
                        Toast.makeText(context,"Lỗi lưu file!", Toast.LENGTH_SHORT).show()
                }
                else->{

                }
            }
        }

        // Drawing canvas
        DrawingCanvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onSizeChanged { size ->
                    canvasSize = size
                },
            viewModel = viewModel
        )

        // Color picker and stroke width controls
        DrawingControls(viewModel)
    }
}

@Composable
fun DrawingControls(viewModel: DrawingViewModel) {
    val strokeColor by viewModel.strokeColor.collectAsState()
    val strokeWidth by viewModel.strokeWidth.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Color selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val colors = listOf(
                    Color.White, Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Magenta, Color.Black
                )

            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 2.dp,
                            color = if (color == strokeColor) Color.Gray else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.setStrokeColor(color) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stroke width slider
        Text("Stroke Width: ${strokeWidth.toInt()}")
        Slider(
            value = strokeWidth,
            onValueChange = { viewModel.setStrokeWidth(it) },
            valueRange = 1f..30f
        )
    }
}
