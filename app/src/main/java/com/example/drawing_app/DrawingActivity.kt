package com.example.drawing_app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var db: DrawingDatabase
    private var currentDrawingId: Long = -1L
    private lateinit var colorButtons: List<ImageButton>
    private lateinit var brushSizeSeekBar: SeekBar
    private var isSaving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        db = DrawingDatabase(this)
        drawingView = findViewById(R.id.drawingView)
        brushSizeSeekBar = findViewById(R.id.brushSizeSeekBar)

        val drawingId = intent.getLongExtra("drawingId", -1L)
        if (drawingId != -1L) {
            currentDrawingId = drawingId
            lifecycleScope.launch {
                loadDrawingAsync(drawingId)
            }
        }

        setupColorButtons()
        setupBrushSizeSeekBar()
        setupButtons()
    }

    private suspend fun loadDrawingAsync(drawingId: Long) {
        withContext(Dispatchers.Default) {
            try {
                val drawing = db.getDrawing(drawingId)
                withContext(Dispatchers.Main) {
                    if (drawing != null) {
                        drawingView.loadBitmap(drawing.bitmap)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Failed to load drawing")
                }
            }
        }
    }

    private fun setupColorButtons() {
        colorButtons = listOf(
            findViewById(R.id.btnColorBlack),
            findViewById(R.id.btnColorRed),
            findViewById(R.id.btnColorBlue),
            findViewById(R.id.btnColorGreen),
            findViewById(R.id.btnColorYellow)
        )

        val colors = listOf(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)

        colorButtons.forEachIndexed { index, button ->
            button.setColorFilter(colors[index])
            button.setOnClickListener {
                drawingView.setColor(colors[index])
            }
        }
    }

    private fun setupBrushSizeSeekBar() {
        brushSizeSeekBar.min = 2
        brushSizeSeekBar.max = 50
        brushSizeSeekBar.progress = 5
        brushSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupButtons() {
        val btnUndo: Button = findViewById(R.id.btnUndo)
        btnUndo.setOnClickListener {
            drawingView.undo()
        }

        val btnRedo: Button = findViewById(R.id.btnRedo)
        btnRedo.setOnClickListener {
            drawingView.redo()
        }

        val btnClear: Button = findViewById(R.id.btnClear)
        btnClear.setOnClickListener {
            drawingView.clear()
        }

        val btnSave: Button = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            saveDrawing()
        }

        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener {
            deleteDrawing()
        }
    }

    private fun saveDrawing() {
        if (isSaving) return
        isSaving = true

        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.Default) {
                    drawingView.getBitmap()
                }

                withContext(Dispatchers.Default) {
                    if (currentDrawingId == -1L) {
                        currentDrawingId = db.saveDrawing(Drawing(
                            id = 0,
                            bitmap = bitmap,
                            timestamp = System.currentTimeMillis()
                        ))
                    } else {
                        db.updateDrawing(Drawing(
                            id = currentDrawingId,
                            bitmap = bitmap,
                            timestamp = System.currentTimeMillis()
                        ))
                    }
                }

                withContext(Dispatchers.Main) {
                    showSuccess("Drawing saved successfully")
                    isSaving = false
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Failed to save drawing: ${e.message}")
                    isSaving = false
                }
            }
        }
    }

    private fun deleteDrawing() {
        if (currentDrawingId == -1L) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    db.deleteDrawing(currentDrawingId)
                }
                withContext(Dispatchers.Main) {
                    showSuccess("Drawing deleted")
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Failed to delete drawing")
                }
            }
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}