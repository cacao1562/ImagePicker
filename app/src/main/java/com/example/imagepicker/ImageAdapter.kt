package com.example.imagepicker

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imagepicker.databinding.ItemImageBinding

class ImageAdapter(private var data: List<Uri>): RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(list: List<Uri>) {
        data = list
        notifyDataSetChanged()
    }
}

class ImageViewHolder(
    private val binding: ItemImageBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri) {
        binding.ivImage.loadImage(uri)
    }

    companion object {
        fun from(parent: ViewGroup): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }
    }
}

fun ImageView.loadImage(imgUri: Uri) {

    Glide.with(context)
        .load(imgUri)
        .apply(
            RequestOptions()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
        )
        .into(this)
}