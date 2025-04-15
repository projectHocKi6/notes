package com.example.mynotes.ui.draw

import androidx.compose.ui.graphics.Color

data class DrawPoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1f
)

data class Stroke(
    val points: List<DrawPoint>,
    val color: Color,
    val strokeWidth: Float
)

data class Drawing(
    val strokes: List<Stroke> = emptyList(),
    val backgroundColor: Color = Color.White
)

