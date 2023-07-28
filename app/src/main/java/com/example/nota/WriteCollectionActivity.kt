package com.example.nota

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner

class WriteCollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_collection)

        val spinner = findViewById<Spinner>(R.id.spinner_collectionCategory)

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories)

        // 스피너 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun onBackButtonClicked(view: View) {
        finish()
    }
}
