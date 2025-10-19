package com.example.drawing_app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class DrawingsAdapter(
    private val drawings: List<Drawing>,
    private val onItemClick: (Drawing) -> Unit
) : RecyclerView.Adapter<DrawingsAdapter.ViewHolder>() {

    inner class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(drawing: Drawing) {
            imageView.setImageBitmap(drawing.bitmap)
            imageView.setOnClickListener { onItemClick(drawing) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(300, 300)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drawings[position])
    }

    override fun getItemCount() = drawings.size
}