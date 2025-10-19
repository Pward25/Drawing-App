package com.example.drawing_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class DrawingDatabase(context: Context) : SQLiteOpenHelper(context, "drawings.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE drawings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bitmap BLOB NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    @Synchronized
    fun saveDrawing(drawing: Drawing): Long {
        return try {
            val db = writableDatabase
            val values = android.content.ContentValues().apply {
                put("bitmap", bitmapToByteArray(drawing.bitmap))
                put("timestamp", drawing.timestamp)
            }
            db.insert("drawings", null, values)
        } catch (e: Exception) {
            throw DrawingDatabaseException("Failed to save drawing", e)
        }
    }

    @Synchronized
    fun updateDrawing(drawing: Drawing) {
        try {
            val db = writableDatabase
            val values = android.content.ContentValues().apply {
                put("bitmap", bitmapToByteArray(drawing.bitmap))
                put("timestamp", drawing.timestamp)
            }
            db.update("drawings", values, "id = ?", arrayOf(drawing.id.toString()))
        } catch (e: Exception) {
            throw DrawingDatabaseException("Failed to update drawing", e)
        }
    }

    @Synchronized
    fun deleteDrawing(id: Long) {
        try {
            val db = writableDatabase
            db.delete("drawings", "id = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            throw DrawingDatabaseException("Failed to delete drawing", e)
        }
    }

    @Synchronized
    fun getDrawing(id: Long): Drawing? {
        return try {
            val db = readableDatabase
            val cursor = db.query("drawings", null, "id = ?", arrayOf(id.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                Drawing(
                    id = cursor.getLong(0),
                    bitmap = byteArrayToBitmap(cursor.getBlob(1)),
                    timestamp = cursor.getLong(2)
                ).also { cursor.close() }
            } else {
                cursor.close()
                null
            }
        } catch (e: Exception) {
            throw DrawingDatabaseException("Failed to retrieve drawing", e)
        }
    }

    @Synchronized
    fun getAllDrawings(): List<Drawing> {
        return try {
            val db = readableDatabase
            val cursor = db.query("drawings", null, null, null, null, null, "timestamp DESC")
            val drawings = mutableListOf<Drawing>()
            while (cursor.moveToNext()) {
                drawings.add(Drawing(
                    id = cursor.getLong(0),
                    bitmap = byteArrayToBitmap(cursor.getBlob(1)),
                    timestamp = cursor.getLong(2)
                ))
            }
            cursor.close()
            drawings
        } catch (e: Exception) {
            throw DrawingDatabaseException("Failed to retrieve drawings", e)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 85, stream)
        return stream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            ?: throw DrawingDatabaseException("Failed to decode bitmap")
    }
}

class DrawingDatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)