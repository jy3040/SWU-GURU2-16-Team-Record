package com.example.nota

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ModifyWishActivity : AppCompatActivity() {

    private lateinit var editTextWishTitle: EditText
    private lateinit var editTextContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_wish)

        val buttonWishModifyCategory = findViewById<Button>(R.id.button_wishModifyCategory)
        val buttonWishModifyCategory2 = findViewById<Button>(R.id.button_wishModifyCategory2)
        val editTextWishCategory = findViewById<EditText>(R.id.editText_wishCategory)

        val db = FirebaseFirestore.getInstance()

        val spinner = findViewById<Spinner>(R.id.spinner_wishCategory)
        val buttonEnter = findViewById<Button>(R.id.button_enter)
        editTextWishTitle = findViewById<EditText>(R.id.editText_wishTitle)
        editTextContent = findViewById<EditText>(R.id.editText_content)

        var email: String

        var selectedCategory = ""

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories).toMutableList()

        val spinnerAdapter =
            ArrayAdapter(this, R.layout.spinner_textstyle, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

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

        val wishData = intent.getSerializableExtra("wishData") as? WishData
        if (wishData != null) {
            editTextWishTitle.setText(wishData.title)
            editTextContent.setText(wishData.content)
        }
        email = wishData!!.email

        buttonEnter.setOnClickListener {
            // EditText에서 문자열을 가져와 hashMap으로 만듦
            val collectionTitle = editTextWishTitle.text.toString()
            if (collectionTitle.isNotEmpty()) {
                val data = hashMapOf(
                    "category" to selectedCategory,
                    "title" to collectionTitle,
                    "content" to editTextContent.text.toString(),
                    "checked" to false
                )
                val dataAsMap: Map<String, Any> = data
                if (selectedCategory == "카테고리를 선택하십시오") {
                    // 경고 창을 띄웁니다.
                    Toast.makeText(this, "유효한 카테고리를 선택하십시오.", Toast.LENGTH_SHORT).show()
                } else {
                    val documentPath = "$email" + "_wish/$collectionTitle"
                    db.document(documentPath)
                        .update(dataAsMap)
                        .addOnSuccessListener {
                            // 성공할 경우
                            Toast.makeText(this, "데이터가 수정되었습니다", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            // 실패할 경우
                            Log.w("WriteWishActivity", "Error getting documents: $exception")
                        }
                }
            } else {
                Toast.makeText(this, "위시 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 버튼 클릭을 통해 카테고리 추가 버튼 visible로
        buttonWishModifyCategory.setOnClickListener {
            // 다른 버튼과 텍스트뷰들을 보이도록 설정
            buttonWishModifyCategory2.visibility = View.VISIBLE
            editTextWishCategory.visibility = View.VISIBLE
            buttonWishModifyCategory.visibility = View.GONE
        }
        buttonWishModifyCategory2.setOnClickListener {
            // 새로운 카테고리 추가 버튼이 클릭되면 호출되는 리스너

            val newCategory = editTextWishCategory.text.toString().trim()

            // 새로운 카테고리가 비어있지 않으면 추가
            if (!TextUtils.isEmpty(newCategory)) {
                categories.add(newCategory)

                // 스피너 어댑터가 아닌 직접 스피너에 카테고리 배열을 설정
                spinnerAdapter.notifyDataSetChanged()

                // 새로운 카테고리를 스피너에서 선택하도록 설정
                spinner.setSelection(categories.size - 1)

                // 에디트 텍스트 비우기
                editTextWishCategory.text = null
            }

            // 버튼과 에디트 텍스트들을 다시 숨기도록 설정
            buttonWishModifyCategory2.visibility = View.GONE
            editTextWishCategory.visibility = View.GONE
            buttonWishModifyCategory.visibility = View.VISIBLE
        }
    }

    fun onBackButtonClicked(view: View) {
        finish()
    }
}
