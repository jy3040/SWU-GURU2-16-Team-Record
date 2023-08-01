package com.example.nota

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(private val images: MutableList<Uri>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_BUTTON = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_grid_list, parent, false)
                ImageViewHolder(view)
            }
            VIEW_TYPE_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_grid_button, parent, false)
                ButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_IMAGE -> {
                val imageUri = images[position]
                (holder as ImageViewHolder).imageView.setImageURI(imageUri)
                Glide.with(holder.itemView.context)
                    .load(imageUri)
                    .centerCrop()
                    .into(holder.imageView)
            }
            VIEW_TYPE_BUTTON -> {
                (holder as ButtonViewHolder).button_addImage.setOnClickListener {
                    // 버튼을 클릭했을 때, 갤러리에서 이미지를 선택하도록 알림
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    if (holder.itemView.context is AppCompatActivity) {
                        (holder.itemView.context as AppCompatActivity).startActivityForResult(intent, 1001)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        // 이미지 개수에 버튼을 하나 추가한 값을 반환
        return images.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < images.size) {
            VIEW_TYPE_IMAGE
        } else {
            VIEW_TYPE_BUTTON
        }
    }

    fun addImage(imageUri: Uri) {
        images.add(imageUri)
        notifyItemInserted(images.size)
    }

    fun getImageUrls(): List<String> {
        return images.map { it.toString() }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_collection_album_image)
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button_addImage: Button = itemView.findViewById(R.id.button_addImage)
    }

}