package com.example.nota

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class WriteCollectionActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView_image: RecyclerView

    private var selectedCategory = ""
    private lateinit var editText_collectionTitle: EditText

    // 추가적인 레이아웃 요소들
    private lateinit var linearLayout9: ConstraintLayout
    private lateinit var linearLayout10: ConstraintLayout
    private lateinit var linearLayout11: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_collection)

        // 스피너와 데이터베이스 초기화
        val spinner = findViewById<Spinner>(R.id.spinner_collectionCategory)
        val db = FirebaseFirestore.getInstance()

        // 버튼 및 에디트 텍스트 초기화
        val button_collectionAddCategory = findViewById<Button>(R.id.button_collectionAddCategory)
        val button_collectionAddCategory2 = findViewById<Button>(R.id.button_collectionAddCategory2)
        val editText_collectionCategory = findViewById<EditText>(R.id.editText_collectionCategory)

        val button_enter = findViewById<Button>(R.id.button_enter)
        editText_collectionTitle = findViewById<EditText>(R.id.editText_collectionTitle)
        val editText_content = findViewById<EditText>(R.id.editText_content)
        val ratingStar = findViewById<RatingBar>(R.id.ratingStar)
        val editText_YYYY = findViewById<EditText>(R.id.editText_YYYY)
        val editText_MM = findViewById<EditText>(R.id.editText_MM)
        val editText_DD = findViewById<EditText>(R.id.editText_DD)

        val editText_optionTitle = findViewById<EditText>(R.id.editText_optionTitle)
        val editText_optionContent = findViewById<EditText>(R.id.editText_optionContent)

        val editText_optionTitle2 = findViewById<EditText>(R.id.editText_optionTitle2)
        val editText_optionContent2 = findViewById<EditText>(R.id.editText_optionContent2)

        val editText_optionTitle3 = findViewById<EditText>(R.id.editText_optionTitle3)
        val editText_optionContent3 = findViewById<EditText>(R.id.editText_optionContent3)

        // 인증 및 파이어스토어 관련 초기화
        val auth = FirebaseAuth.getInstance()
        val fbFirestore = FirebaseFirestore.getInstance()
        val uid = auth.uid
        val collectionRef = fbFirestore?.collection("user")?.document("$uid")
        var email: String

        // 추가 옵션 레이아웃 초기화
        linearLayout9 = findViewById(R.id.linearLayout9)
        linearLayout10 = findViewById(R.id.linearLayout10)
        linearLayout11 = findViewById(R.id.linearLayout11)
        hideAllLayouts()

        // 이미지 리사이클러 뷰 초기화
        recyclerView_image = findViewById(R.id.recyclerView_image)
        imageAdapter = ImageAdapter(mutableListOf())
        recyclerView_image.adapter = imageAdapter
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView_image.layoutManager = gridLayoutManager

        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories).toMutableList()

        // 스피너 어댑터 설정
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_textstyle, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // 카테고리 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = categories[position]
                selectedCategory = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // 버튼 클릭 시 데이터베이스에 저장
        button_enter.setOnClickListener {
            collectionRef?.get()?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email = documentSnapshot.getString("email").toString()
                    val collectionTitle = editText_collectionTitle.text.toString()
                    if (collectionTitle.isNotEmpty()) {
                        // 파이어스토어에서 입력한 문서 이름이 있는지 확인합니다.
                        db.collection("$email" + "_collection")
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
                                        val imageUrls = imageAdapter.getImageUrls()
                                        val imageUrl: MutableList<Uri> = mutableListOf()
                                        for (url in imageUrls) {
                                            imageUrl.add(Uri.parse(url))
                                        }
                                        uploadImagesAndGetUrls(imageUrl, collectionTitle)
                                        // EditText에서 문자열을 가져와 hashMap으로 만듦
                                        val data = hashMapOf(
                                            "category" to selectedCategory,
                                            "title" to collectionTitle,
                                            "content" to editText_content.text.toString(),
                                            "rating" to ratingStar.rating,
                                            "Y" to editText_YYYY.text.toString().toInt(),
                                            "M" to editText_MM.text.toString().toInt(),
                                            "D" to editText_DD.text.toString().toInt(),
                                            "optiontitle" to editText_optionTitle.text.toString(),
                                            "optioncontent" to editText_optionContent.text.toString(),
                                            "optiontitle2" to editText_optionTitle2.text.toString(),
                                            "optioncontent2" to editText_optionContent2.text.toString(),
                                            "optiontitle3" to editText_optionTitle3.text.toString(),
                                            "optioncontent3" to editText_optionContent3.text.toString(),
                                            "images" to imageUrls
                                        )
                                        if (selectedCategory == "카테고리를 선택하십시오") {
                                            // 경고 창을 띄웁니다.
                                            Toast.makeText(
                                                this,
                                                "유효한 카테고리를 선택하십시오.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            // 데이터베이스에 데이터 저장
                                            db.collection("$email" + "_collection")
                                                .document(collectionTitle)
                                                .set(data)
                                                .addOnSuccessListener {
                                                    // 성공할 경우
                                                    Toast.makeText(
                                                        this,
                                                        "데이터가 추가되었습니다",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    var intent =
                                                        Intent(this, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                                .addOnFailureListener { exception ->
                                                    // 실패할 경우
                                                    Log.w(
                                                        "WriteCollectionActivity",
                                                        "Error getting documents: $exception"
                                                    )
                                                }

                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
        // 카테고리 추가 버튼 클릭 리스너
        button_collectionAddCategory.setOnClickListener {
            // 다른 버튼과 텍스트뷰들을 보이도록 설정
            button_collectionAddCategory2.visibility = View.VISIBLE
            editText_collectionCategory.visibility = View.VISIBLE
            button_collectionAddCategory.visibility = View.GONE
        }

// 새로운 카테고리 추가 버튼 클릭 리스너
        button_collectionAddCategory2.setOnClickListener {
            // 새로운 카테고리 추가 버튼이 클릭되면 호출되는 리스너

            val newCategory = editText_collectionCategory.text.toString().trim()

            // 새로운 카테고리가 비어있지 않으면 추가
            if (!TextUtils.isEmpty(newCategory)) {
                categories.add(newCategory)

                // 스피너 어댑터가 아닌 직접 스피너에 카테고리 배열을 설정
                spinnerAdapter.notifyDataSetChanged()

                // 새로운 카테고리를 스피너에서 선택하도록 설정
                spinner.setSelection(categories.size - 1)

                // 에디트 텍스트 비우기
                editText_collectionCategory.text = null
            }

            // 버튼과 에디트 텍스트들을 다시 숨기도록 설정
            button_collectionAddCategory2.visibility = View.GONE
            editText_collectionCategory.visibility = View.GONE
            button_collectionAddCategory.visibility = View.VISIBLE
        }
    }

        // 이미지 선택 후 호출되는 메소드
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

// 추가 레이아웃을 화면에 보여주는 메소드
        private var layoutCounter = 1
        fun addLayout(view: View) {
            when (layoutCounter) {
                1 -> linearLayout9.visibility = View.VISIBLE
                2 -> linearLayout10.visibility = View.VISIBLE
                3 -> linearLayout11.visibility = View.VISIBLE
                // 더 이상 추가할 레이아웃이 없으면 기존 레이아웃을 숨기지 않고 함수 종료
                else -> return
            }
            layoutCounter++
        }

        // 모든 레이아웃들을 숨기는 메소드
        private fun hideAllLayouts() {
            linearLayout9.visibility = View.GONE
            linearLayout10.visibility = View.GONE
            linearLayout11.visibility = View.GONE
        }

// ActivityResultContract를 통해 이미지 선택 결과를 처리하도록 되어있음
        companion object {
            private const val REQUEST_CODE_PICK_IMAGE = 1001
        }

        // 뒤로 가기 버튼 클릭 시 현재 액티비티를 종료하고 이전 액티비티로 돌아가기 위한 메소드
        fun onBackButtonClicked(view: View) {
            finish()
        }

        // 이미지를 업로드하고 이미지 URL을 받아오는 메소드
        private fun uploadImagesAndGetUrls(imageUrls: List<Uri>, collectionTitle: String) {
            val storage = FirebaseStorage.getInstance()

            val email = FirebaseAuth.getInstance().currentUser?.email

            val imageUrlsList = mutableListOf<String>()

            var num = 0
            for (i in imageUrls) {
                val imgFileName = "$num.jpg"
                val imageRef = storage.reference.child("$email").child("$collectionTitle")
                    .child("$imgFileName")
                num++

                imageRef.putFile(i)
                    .addOnSuccessListener {
                        // 이미지 업로드 성공 시 이미지 URL을 받아옴
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageUrlsList.add(uri.toString())
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 이미지 업로드 실패 시 동작할 코드
                        // 실패한 경우는 이미지를 더 업로드하지 않고 종료
                        Toast.makeText(this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
}

