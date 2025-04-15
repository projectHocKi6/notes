package com.example.mynotes.ui.draw

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.drawing.DrawingEntity
import com.example.mynotes.data.drawing.DrawingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

class DrawingViewModel(private val drawingsRepository: DrawingsRepository) : ViewModel() {
    // Current drawing state
    private val _drawing = MutableStateFlow(Drawing())
    val drawing: StateFlow<Drawing> = _drawing
    // Current stroke being drawn
    private val _currentStroke = MutableStateFlow<Stroke?>(null)

    // Current drawing settings
    private val _strokeColor = MutableStateFlow(Color.Black)
    val strokeColor: StateFlow<Color> = _strokeColor

    private val _strokeWidth = MutableStateFlow(5f)
    val strokeWidth: StateFlow<Float> = _strokeWidth

    // Drawing title
    private val _drawingTitle = MutableStateFlow("")
    val drawingTitle: StateFlow<String> = _drawingTitle

    // Rest of your existing drawing methods...

    fun startStroke(x: Float, y: Float) {
        _currentStroke.value = Stroke(
            points = listOf(DrawPoint(x, y)),
            color = _strokeColor.value,
            strokeWidth = _strokeWidth.value
        )
    }

    // Add a point to the current stroke
    fun addPointToStroke(x: Float, y: Float, pressure: Float = 1f) {
        _currentStroke.value?.let { currentStroke ->
            val updatedPoints = currentStroke.points + DrawPoint(x, y, pressure)
            _currentStroke.value = currentStroke.copy(points = updatedPoints)
        }
    }

    // Finish the current stroke and add it to the drawing
    fun endStroke() {
        _currentStroke.value?.let { stroke ->
            if (stroke.points.size > 1) {
                val updatedStrokes = _drawing.value.strokes + stroke
                _drawing.value = _drawing.value.copy(strokes = updatedStrokes)
            }
            _currentStroke.value = null
        }
    }

    // Change drawing settings
    fun setStrokeColor(color: Color) {
        _strokeColor.value = color
    }

    fun setStrokeWidth(width: Float) {
        _strokeWidth.value = width
    }
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // id của file đã cần đọc
    private var currentDrawingId: Long? = null

    private var currentFilePath: String? = null
    // Clear the drawing
    fun clearDrawing() {
        _drawing.value = Drawing()
    }
    fun getDrawingAsBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Fill background
        val paint = Paint()
        paint.color = _drawing.value.backgroundColor.toArgb()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Draw all strokes
        _drawing.value.strokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path()
                path.moveTo(stroke.points.first().x, stroke.points.first().y)

                for (i in 1 until stroke.points.size) {
                    path.lineTo(stroke.points[i].x, stroke.points[i].y)
                }

                paint.color = stroke.color.toArgb()
                paint.strokeWidth = stroke.strokeWidth
                paint.style = Paint.Style.STROKE
                paint.strokeJoin = Paint.Join.ROUND
                paint.strokeCap = Paint.Cap.ROUND

                canvas.drawPath(path, paint)
            }
        }

        return bitmap
    }
    // Save drawing
    fun saveDrawing(bitmap: Bitmap) {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Success

            try {
                // Generate file path and name
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "drawing_$timestamp.png"

                // Get the app's private storage directory
                val file = withContext(Dispatchers.IO) {
                    val storageDir = File(
                        File(System.getProperty("java.io.tmpdir")),
                        "drawings"
                    )

                    // Create directories if needed
                    if (!storageDir.exists()) {
                        storageDir.mkdirs()
                    }

                    File(storageDir, fileName)
                }

                // Save bitmap to file
                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }

                // Create drawing entity and save to database
                val drawing = DrawingEntity(
                    id = 0, // Auto-generated ID
                    title = "Drawing $timestamp",
                    filePath = file.absolutePath,
                    createdAt = System.currentTimeMillis()
                )

                val drawingId = withContext(Dispatchers.IO) {
                    drawingsRepository.insertDrawing(drawing)
                }

                Log.d("DrawingViewModel", "Drawing saved with ID: $drawingId")
                _saveStatus.value = SaveStatus.Success
            } catch (e: Exception) {
                Log.e("DrawingViewModel", "Error saving drawing", e)
                _saveStatus.value = SaveStatus.Error(e.message ?: "Unknown error")
            }
        }
    }
    // Load existing drawing
    fun loadDrawing(drawingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val drawing = withContext(Dispatchers.IO) {
                    drawingsRepository.getDrawingById(drawingId)
                }

                if (drawing != null) {
                    currentDrawingId = drawing.id.toLong()
                    currentFilePath = drawing.filePath
                    _drawingTitle.value = drawing.title
                    _isLoading.value = false
                } else {
                    Log.e("DrawingViewModel", "Drawing not found")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("DrawingViewModel", "Error loading drawing", e)
                _isLoading.value = false
            }
        }
    }
    // Status for saving operation
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }

    // Get current user UID - implement based on your auth system
    private fun getCurrentUserUID(): String {
        // TODO: Implement based on your authentication system
        return ""
    }

    // Data class for drawing paths
    data class PathData(
        val path: Path,
        val color: Color,
        val strokeWidth: Float
    )

    // Drawing paths
    private val _paths = MutableStateFlow<List<PathData>>(emptyList())
    val paths: StateFlow<List<PathData>> = _paths
    private var currentPath = Path()

    // Stroke color and width settings



    // Start a new path
    fun startDrawing(start: Offset) {
        currentPath = Path().apply {
            moveTo(start.x, start.y)
        }
    }

    // Add a point to the current path
    fun continueDrawing(point: Offset) {
        val lastPoint = currentPath.lastLineTo ?: return
        if ((point - lastPoint).getDistance() > 4) {
            currentPath.lineTo(point.x, point.y)

            // Update the paths list with the current path
            val newPaths = _paths.value.toMutableList()

            // Remove the last path (which is our current path) if it exists
            if (newPaths.isNotEmpty()) {
                newPaths.removeAt(newPaths.size - 1)
            }

            // Add the updated current path
            newPaths.add(
                PathData(
                    path = currentPath,
                    color = _strokeColor.value,
                    strokeWidth = _strokeWidth.value
                )
            )

            _paths.value = newPaths
        }
    }

    // Finish the current path
    fun finishDrawing() {
        if (!currentPath.isEmpty) {
            val newPaths = _paths.value.toMutableList()
            newPaths.add(
                PathData(
                    path = currentPath,
                    color = _strokeColor.value,
                    strokeWidth = _strokeWidth.value
                )
            )
            _paths.value = newPaths
            currentPath = Path()
        }
    }
}

val Path.lastLineTo: Offset?
    get() = this::class.java.getDeclaredField("commands").let { field ->
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val commands = field.get(this) as? ArrayList<Any>
        commands?.lastOrNull()?.let { lastCommand ->
            val cx = lastCommand::class.java.getDeclaredField("x")
            val cy = lastCommand::class.java.getDeclaredField("y")
            cx.isAccessible = true
            cy.isAccessible = true
            Offset(cx.getFloat(lastCommand), cy.getFloat(lastCommand))
        }
    }

// Extension function to calculate distance between two offsets
fun Offset.getDistance(other: Offset): Float {
    return sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y))
}