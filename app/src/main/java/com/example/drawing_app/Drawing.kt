package com.example.drawing_app

import android.graphics.Bitmap

data class Drawing(
    val id: Long,
    val bitmap: Bitmap,
    val timestamp: Long
)