package com.example.drawing_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var drawingsAdapter: DrawingsAdapter
    private lateinit var db: DrawingDatabase
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DrawingDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, DrawingActivity::class.java)
            startActivity(intent)
        }

        loadDrawings()
    }

    override fun onResume() {
        super.onResume()
        loadDrawings()
    }

    private fun loadDrawings() {
        lifecycleScope.launch {
            try {
                val drawings = withContext(Dispatchers.Default) {
                    db.getAllDrawings()
                }

                withContext(Dispatchers.Main) {
                    drawingsAdapter = DrawingsAdapter(drawings) { drawing ->
                        val intent = Intent(this@MainActivity, DrawingActivity::class.java)
                        intent.putExtra("drawingId", drawing.id)
                        startActivity(intent)
                    }
                    recyclerView.adapter = drawingsAdapter
                }
            } catch (e: DrawingDatabaseException) {
                withContext(Dispatchers.Main) {
                    showError("Failed to load drawings: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("An unexpected error occurred")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}