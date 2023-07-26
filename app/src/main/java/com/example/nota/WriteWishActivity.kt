package com.example.nota

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class WriteWishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_wish)
    }
    fun onBackButtonClicked(view: View) {
        finish()
    }
}