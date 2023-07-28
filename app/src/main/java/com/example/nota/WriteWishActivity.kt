package com.example.nota

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class WriteWishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_wish)

        val button_wishAddCategory = findViewById<Button>(R.id.button_wishAddCategory)
        val button_wishAddCategory2 = findViewById<Button>(R.id.button_wishAddCategory2)
        val editText_wishCategory = findViewById<EditText>(R.id.editText_wishCategory)

        val db = FirebaseFirestore.getInstance()

        val spinner = findViewById<Spinner>(R.id.spinner_wishCategory)
        val button_enter = findViewById<Button>(R.id.button_enter)
        var editText_collectionTitle = findViewById<EditText>(R.id.editText_wishTitle)
        var editText_content = findViewById<EditText>(R.id.editText_content)

        var selectedCategory=""

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories)

        // 스피너 어댑터 설정
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        val adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_textstyle)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        //카테고리 선택
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = categories[position]
                // 선택된 항목에 대한 처리를 수행합니다.
                selectedCategory = selectedItem
                // 예: 선택된 항목을 로그로 출력하거나, 변수에 저장합니다.
                Log.d("Selected Item", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 선택이 해제되었을 때 수행할 동작을 정의합니다.
            }
        }

        // 완료 버튼을 통해 데이터베이스에 저장
        button_enter.setOnClickListener {
            // EditText에서 문자열을 가져와 hashMap으로 만듦
            val data = hashMapOf(
                "category" to selectedCategory,
                "title" to editText_collectionTitle.text.toString(),
                "content" to editText_content.text.toString()
            )
            // Contacts 컬렉션에 data를 자동 이름으로 저장
            db.collection("Wish")
                .add(data)
                .addOnSuccessListener {
                    // 성공할 경우
                    Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("WriteWishActivity", "Error getting documents: $exception")
                }
        }
        // 버튼 클릭을 통해 카테고리 추가 버튼 visible로
        button_wishAddCategory.setOnClickListener {
            // 다른 버튼과 텍스트뷰들을 보이도록 설정
            button_wishAddCategory2.visibility = View.VISIBLE
            editText_wishCategory.visibility = View.VISIBLE
            button_wishAddCategory.visibility = View.GONE
        }
        button_wishAddCategory2.setOnClickListener {
            button_wishAddCategory2.visibility = View.GONE
            editText_wishCategory.visibility = View.GONE
            button_wishAddCategory.visibility = View.VISIBLE
            //카테고리를 추가하는 명령어 여기다가 작성하면 됩니당~~~
        }
    }

    fun onBackButtonClicked(view: View) {
        finish()
    }
}
