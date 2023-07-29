package com.example.nota

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class WriteCollectionActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView_image: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_collection)

        val spinner = findViewById<Spinner>(R.id.spinner_collectionCategory)
        val db = FirebaseFirestore.getInstance()

        val button_collectionAddCategory = findViewById<Button>(R.id.button_collectionAddCategory)
        val button_collectionAddCategory2 = findViewById<Button>(R.id.button_collectionAddCategory2)
        val editText_collectionCategory = findViewById<EditText>(R.id.editText_collectionCategory)

        val button_enter = findViewById<Button>(R.id.button_enter)
        var editText_collectionTitle = findViewById<EditText>(R.id.editText_collectionTitle)
        var editText_content = findViewById<EditText>(R.id.editText_content)
        var ratingStar = findViewById<RatingBar>(R.id.ratingStar)
        var editText_YYYY = findViewById<EditText>(R.id.editText_YYYY)
        var editText_MM = findViewById<EditText>(R.id.editText_MM)
        var editText_DD = findViewById<EditText>(R.id.editText_DD)

        // 이미지 리사이클러 뷰에 저장하기 위한 코드 시작
        recyclerView_image = findViewById(R.id.recyclerView_image)
        imageAdapter = ImageAdapter(mutableListOf())
        recyclerView_image.adapter = imageAdapter

        // GridLayout으로 변경 (spanCount는 한 줄에 표시될 열의 수를 결정)
        val gridLayoutManager = GridLayoutManager(this, 3) // 3열로 표시하도록 설정
        recyclerView_image.layoutManager = gridLayoutManager
        //----------------------------------------------------
        var selectedCategory=""

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories)

        // 스피너 어댑터 설정
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        val adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_textstyle)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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
        // 버튼 클릭 시에 데이터베이스에 저장
        button_enter.setOnClickListener {
            // EditText에서 문자열을 가져와 hashMap으로 만듦
            val data = hashMapOf(
                "category" to selectedCategory,
                "title" to editText_collectionTitle.text.toString(),
                "content" to editText_content.text.toString(),
                "rating" to ratingStar.rating.toLong(),
                "Y" to editText_YYYY.text.toString().toInt(),
                "M" to editText_MM.text.toString().toInt(),
                "D" to editText_DD.text.toString().toInt(),
                "image" to "image_url"
            )
            // Contacts 컬렉션에 data를 자동 이름으로 저장
            db.collection("Collection")
                .add(data)
                .addOnSuccessListener {
                    // 성공할 경우
                    Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("WriteCollectionActivity", "Error getting documents: $exception")
                }
            if (selectedCategory == "카테고리를 선택하십시오") {
                // 경고 창을 띄웁니다.
                Toast.makeText(this, "유효한 카테고리를 선택하십시오.", Toast.LENGTH_SHORT).show()
            } else {
                // Contacts 컬렉션에 data를 자동 이름으로 저장
                db.collection("Collection")
                    .add(data)
                    .addOnSuccessListener {
                        // 성공할 경우
                        Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우
                        Log.w("WriteCollectionActivity", "Error getting documents: $exception")
                    }
            }
        }

        // 버튼 클릭을 통해 카테고리 추가 버튼 visible로
        button_collectionAddCategory.setOnClickListener {
            // 다른 버튼과 텍스트뷰들을 보이도록 설정
            button_collectionAddCategory2.visibility = View.VISIBLE
            editText_collectionCategory.visibility = View.VISIBLE
            button_collectionAddCategory.visibility = View.GONE
        }
        button_collectionAddCategory2.setOnClickListener {
            button_collectionAddCategory2.visibility = View.GONE
            editText_collectionCategory.visibility = View.GONE
            button_collectionAddCategory.visibility = View.VISIBLE
            //카테고리를 추가하는 명령어 여기다가 작성하면 됩니당~~~
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUri: Uri? = data.data
                if (imageUri != null) {
                    // 선택된 이미지를 RecyclerView에 추가합니다.
                    imageAdapter.addImage(imageUri)
                }
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
    fun onBackButtonClicked(view: View) {
        finish()
    }

}
