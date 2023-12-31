package com.example.nota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WriteWishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_wish)

        // View 요소들 초기화
        val button_wishAddCategory = findViewById<Button>(R.id.button_wishAddCategory)
        val button_wishAddCategory2 = findViewById<Button>(R.id.button_wishAddCategory2)
        val editText_wishCategory = findViewById<EditText>(R.id.editText_wishCategory)
        val db = FirebaseFirestore.getInstance()
        val spinner = findViewById<Spinner>(R.id.spinner_wishCategory)
        val button_enter = findViewById<Button>(R.id.button_enter)
        val editText_collectionTitle = findViewById<EditText>(R.id.editText_wishTitle)
        val editText_content = findViewById<EditText>(R.id.editText_content)

        // Firebase 인스턴스 및 사용자 UID 가져오기
        val auth = FirebaseAuth.getInstance()
        val fbFirestore = FirebaseFirestore.getInstance()
        val uid = auth.uid

        // Firestore의 "user" 컬렉션에서 해당 사용자 문서 가져오기
        val collectionRef = fbFirestore?.collection("user")?.document("$uid")
        var email: String

        // 선택된 카테고리를 저장하기 위한 변수
        var selectedCategory = ""

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories).toMutableList()

        // 스피너 어댑터 설정 및 스피너에 어댑터 적용
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_textstyle, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // 카테고리 선택 이벤트 리스너 설정
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
            collectionRef?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        email = documentSnapshot.getString("email").toString()
                        val collectionTitle = editText_collectionTitle.text.toString()
                        if (collectionTitle.isNotEmpty()) {
                            // 파이어스토어에서 입력한 문서 이름이 있는지 확인합니다.
                            db.collection("$email" + "_wish")
                                .document(collectionTitle)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document != null && document.exists()) {
                                            // 이미 문서 이름이 존재하는 경우
                                            Toast.makeText(
                                                this,
                                                "이미 존재하는 컬렉션 제목입니다. 다른 제목을 입력해주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            // 컬렉션 제목이 존재하지 않는 경우
                                            val data = hashMapOf(
                                                "category" to selectedCategory,
                                                "title" to collectionTitle,
                                                "content" to editText_content.text.toString(),
                                                "checked" to false
                                            )
                                            if (selectedCategory == "카테고리를 선택하십시오") {
                                                // 경고 창을 띄웁니다.
                                                Toast.makeText(
                                                    this,
                                                    "유효한 카테고리를 선택하십시오.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                db.collection("$email" + "_wish")
                                                    .document(collectionTitle)
                                                    .set(data)
                                                    .addOnSuccessListener {
                                                        // 성공할 경우
                                                        Toast.makeText(
                                                            this,
                                                            "데이터가 추가되었습니다",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        var intent = Intent(this, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        // 실패할 경우
                                                        Log.w(
                                                            "WriteWishActivity",
                                                            "Error getting documents: $exception"
                                                        )
                                                    }
                                            }
                                        }
                                    } else {
                                        Log.w(
                                            "WriteWishActivity",
                                            "Error getting documents: ${task.exception}"
                                        )
                                    }
                                }
                        } else {
                            Toast.makeText(this, "컬렉션 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        // 버튼 클릭을 통해 카테고리 추가 버튼 visible로
        button_wishAddCategory.setOnClickListener {
            // 다른 버튼과 텍스트뷰들을 보이도록 설정
            button_wishAddCategory2.visibility = View.VISIBLE
            editText_wishCategory.visibility = View.VISIBLE
            button_wishAddCategory.visibility = View.GONE
        }

        // 새로운 카테고리 추가 버튼 클릭 리스너
        button_wishAddCategory2.setOnClickListener {
            val newCategory = editText_wishCategory.text.toString().trim()
            // 새로운 카테고리가 비어있지 않으면 추가
            if (!TextUtils.isEmpty(newCategory)) {
                categories.add(newCategory)
                // 스피너 어댑터가 아닌 직접 스피너에 카테고리 배열을 설정
                spinnerAdapter.notifyDataSetChanged()
                // 새로운 카테고리를 스피너에서 선택하도록 설정
                spinner.setSelection(categories.size - 1)
                // 에디트 텍스트 비우기
                editText_wishCategory.text = null
            }
            // 버튼과 에디트 텍스트들을 다시 숨기도록 설정
            button_wishAddCategory2.visibility = View.GONE
            editText_wishCategory.visibility = View.GONE
            button_wishAddCategory.visibility = View.VISIBLE
        }
    }

    // 뒤로 가기 버튼 클릭 시 현재 액티비티를 종료하고 이전 액티비티로 돌아가기 위한 메소드
    fun onBackButtonClicked(view: View) {
        finish()
    }
}