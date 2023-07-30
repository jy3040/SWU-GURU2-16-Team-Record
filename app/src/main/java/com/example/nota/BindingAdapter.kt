package com.example.nota

import PhotoAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage

object BindingAdapter {
    fun loadImage(imageView: ImageView, user: String){

        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://nota-89a90.appspot.com")
        val storageReference = storage.reference
        val pathReference = storageReference.child("images/PROFILE_"+user+"_png")

        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imageView.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(imageView)
        }
    }
    fun loadImage(imageView: ImageView, title: String, email: String){
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://nota-89a90.appspot.com")
        val storageReference = storage.reference
        val pathReference = storageReference.child("$email").child("$title").child("0.jpg")

        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imageView.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(imageView)
        }
    }
    fun loadImage(imageView: RecyclerView, title: String, email: String,num:Int){
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://nota-89a90.appspot.com")
        val storageReference = storage.reference

        val photoUrls = mutableListOf<String>()

        for (i in 0 until num) {
            val pathReference = storageReference.child("$email").child("$title").child("$i.jpg")
            pathReference.downloadUrl.addOnSuccessListener { uri ->
                // 이미지 URL을 리스트에 추가
                photoUrls.add(uri.toString())

                // 모든 이미지 URL들을 가져왔을 때만 어댑터를 생성하고 설정
                if (photoUrls.size == num) {
                    val photoAdapter = PhotoAdapter(photoUrls)
                    imageView.adapter = photoAdapter
                }
            }
        }
    }
}