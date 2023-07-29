package com.example.nota

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.siyamed.shapeimageview.RoundedImageView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class CollectionAdapter(private val collectionList: List<CollectionData>) :
    RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    // View Holder 클래스
    class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_list_collection_img: ImageView = itemView.findViewById(R.id.iv_list_collection_img)
        val tv_list_collection_title: TextView = itemView.findViewById(R.id.tv_list_collection_title)
        val rb_list_collection: AppCompatRatingBar= itemView.findViewById(R.id.rb_list_collection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_collection, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collectionData = collectionList[position]

        // 데이터를 뷰홀더의 뷰들에 바인딩
        holder.tv_list_collection_title.text = collectionData.title
        holder.rb_list_collection.rating = collectionData.rating.toFloat()
        Glide.with(holder.itemView.context).load(collectionData.imageUrls[0]).into(holder.iv_list_collection_img)
    }

    override fun getItemCount(): Int {
        return collectionList.size
    }
}