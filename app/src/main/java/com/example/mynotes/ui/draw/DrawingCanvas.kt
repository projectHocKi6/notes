package com.example.mynotes.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.MaterialTheme



@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel
) {
    val paths by viewModel.paths.collectAsState()

    Canvas(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        viewModel.startDrawing(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        viewModel.continueDrawing(change.position)
                    },
                    onDragEnd = {
                        viewModel.finishDrawing()
                    },
                    onDragCancel = {
                        viewModel.finishDrawing()
                    }
                )
            }
    ) {
        // Draw all the paths
        paths.forEach { pathData ->
            drawPath(
                path = pathData.path,
                color = pathData.color,
                style = Stroke(
                    width = pathData.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}