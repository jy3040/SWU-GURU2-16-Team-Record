package com.example.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // 여기에 이미지 데이터 리스트를 선언해주세요
    private val imageList: List<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_grid_list, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // 이미지 데이터를 뷰에 연결하는 로직을 작성해주세요
        // 예: holder.imageView.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 뷰 홀더의 뷰 요소들을 선언해주세요
        // 예: val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}