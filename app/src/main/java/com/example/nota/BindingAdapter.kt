package com.example.nota

import android.widget.ImageView
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
}