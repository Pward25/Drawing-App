package com.example.drawing_app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Stack

class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var lastX = 0f
    private var lastY = 0f
    private var brushSize = 5f

    // Undo/Redo stacks
    private val undoStack = Stack<Bitmap>()
    private val redoStack = Stack<Bitmap>()
    private val maxHistorySize = 20

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (::bitmap.isInitialized) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                // Save state before drawing
                saveBitmapState()
            }
            MotionEvent.ACTION_MOVE -> {
                canvas.drawLine(lastX, lastY, x, y, paint)
                lastX = x
                lastY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Clear redo stack on new action
                redoStack.clear()
            }
        }
        return true
    }

    fun clear() {
        saveBitmapState()
        canvas.drawColor(Color.WHITE)
        redoStack.clear()
        invalidate()
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setBrushSize(size: Float) {
        brushSize = size
        paint.strokeWidth = size
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.push(bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true))
            bitmap = undoStack.pop().copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
            canvas = Canvas(bitmap)
            invalidate()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true))
            bitmap = redoStack.pop().copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
            canvas = Canvas(bitmap)
            invalidate()
        }
    }

    fun getBitmap(): Bitmap = bitmap

    fun loadBitmap(loadedBitmap: Bitmap) {
        bitmap = loadedBitmap.copy(loadedBitmap.config ?: Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap)
        undoStack.clear()
        redoStack.clear()
        invalidate()
    }

    private fun saveBitmapState() {
        if (::bitmap.isInitialized) {
            undoStack.push(bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true))
            if (undoStack.size > maxHistorySize) {
                undoStack.removeAt(0)
            }
        }
    }
}