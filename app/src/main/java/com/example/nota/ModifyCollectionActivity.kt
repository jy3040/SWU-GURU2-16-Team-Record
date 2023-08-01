package com.example.nota

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ModifyCollectionActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView_image: RecyclerView

    private var selectedCategory=""
    private lateinit var editText_collectionTitle: EditText

    private lateinit var linearLayout9: ConstraintLayout
    private lateinit var linearLayout10: ConstraintLayout
    private lateinit var linearLayout11: ConstraintLayout

    private var Image_num: Int = 0
    private val selectedImageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_collection)

        val spinner = findViewById<Spinner>(R.id.spinner_collectionCategory)
        val db = FirebaseFirestore.getInstance()

        val button_collectionAddCategory = findViewById<Button>(R.id.button_collectionModifyCategory)
        val button_collectionAddCategory2 = findViewById<Button>(R.id.button_collectionModifyCategory2)
        val editText_collectionCategory = findViewById<EditText>(R.id.editText_collectionCategory)

        val button_enter = findViewById<Button>(R.id.button_enter)
        editText_collectionTitle = findViewById<EditText>(R.id.editText_collectionTitle)
        var editText_content = findViewById<EditText>(R.id.editText_content)
        var ratingStar = findViewById<RatingBar>(R.id.ratingStar)
        var editText_YYYY = findViewById<EditText>(R.id.editText_YYYY)
        var editText_MM = findViewById<EditText>(R.id.editText_MM)
        var editText_DD = findViewById<EditText>(R.id.editText_DD)

        var editText_optionTitle = findViewById<EditText>(R.id.editText_optionTitle)
        var editText_optionContent = findViewById<EditText>(R.id.editText_optionContent)

        var editText_optionTitle2 = findViewById<EditText>(R.id.editText_optionTitle2)
        var editText_optionContent2 = findViewById<EditText>(R.id.editText_optionContent2)

        var editText_optionTitle3 = findViewById<EditText>(R.id.editText_optionTitle3)
        var editText_optionContent3 = findViewById<EditText>(R.id.editText_optionContent3)

        recyclerView_image = findViewById(R.id.recyclerView_image)

        //받은 데이터 입력되어있는상태
        val collectionData = intent.getSerializableExtra("collectionData") as? CollectionData
        if (collectionData != null) {

            editText_collectionTitle.setText(collectionData.title)
            editText_content.setText(collectionData.content)
            ratingStar.rating = collectionData.rating.toFloat()
            editText_YYYY.setText(collectionData.Y.toString())
            editText_MM.setText(collectionData.M.toString())
            editText_DD.setText(collectionData.D.toString())
            if(collectionData.optiontitle!=null) {
                button_collectionAddCategory2.visibility = View.VISIBLE
                editText_collectionCategory.visibility = View.VISIBLE
                button_collectionAddCategory.visibility = View.GONE
                editText_optionTitle.setText(collectionData.optiontitle)
                editText_optionContent.setText(collectionData.optioncontent)
            }
            if(collectionData.optiontitle2!=null) {
                editText_optionTitle2.setText(collectionData.optiontitle2)
                editText_optionContent2.setText(collectionData.optioncontent2)
            }

            if(collectionData.optiontitle3!=null) {
                editText_optionTitle3.setText(collectionData.optiontitle3)
                editText_optionContent3.setText(collectionData.optioncontent3)
            }
            var Count = collectionData.imageUrls.size
            var email = collectionData.email
            var title = collectionData.title
            //

            BindingAdapter.load(recyclerView_image, title, email, Count)

        }
        Image_num = collectionData!!.imageUrls.size
        val images = collectionData.imageUrls

        val auth = FirebaseAuth.getInstance()
        val fbFirestore = FirebaseFirestore.getInstance()
        val uid = auth.uid

        val collectionRef = fbFirestore?.collection("user")?.document("$uid")
        var email:String

        linearLayout9 = findViewById(R.id.linearLayout9)
        linearLayout10 = findViewById(R.id.linearLayout10)
        linearLayout11 = findViewById(R.id.linearLayout11)

        // 추가 옵션 레이아웃
        hideAllLayouts()

        // 이미지 리사이클러 뷰에 저장하기 위한 코드 시작
        recyclerView_image = findViewById(R.id.recyclerView_image)
        imageAdapter = ImageAdapter(mutableListOf())
        recyclerView_image.adapter = imageAdapter

        // GridLayout으로 변경 (spanCount는 한 줄에 표시될 열의 수를 결정)
        val gridLayoutManager = GridLayoutManager(this, 3) // 3열로 표시하도록 설정
        recyclerView_image.layoutManager = gridLayoutManager
        //----------------------------------------------------
        // 카테고리명 배열 가져오기
        val categories = resources.getStringArray(R.array.categories).toMutableList()

        // 스피너 어댑터 설정
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_textstyle, categories)
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
        // 버튼 클릭 시에 데이터베이스에 저장
        button_enter.setOnClickListener {
            val collectionTitle = editText_collectionTitle.text.toString()
            if (collectionTitle.isNotEmpty()) {
                val imageUrls = imageAdapter.getImageUrls()
                val imageUrl: MutableList<Uri> = mutableListOf()
                for (url in imageUrls) {
                    imageUrl.add(Uri.parse(url))
                }
                uploadImagesAndGetUrls(imageUrl, collectionTitle)
                val data = hashMapOf(
                    "category" to selectedCategory,
                    "title" to editText_collectionTitle.text.toString(),
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
                    "images" to images+selectedImageUris.map { it.toString() } // 이미지 URL 리스트를 저장
                )

                // Firestore에 데이터 저장
                saveDataToFirestore(data)

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUri: Uri? = data.data
                if (imageUri != null) {
                    // 선택된 이미지를 RecyclerView와 selectedImageUris에 추가합니다.
                    imageAdapter.addImage(imageUri)
                    selectedImageUris.add(imageUri)
                }
            }
        }
    }
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

    // 모든 레이아웃들을 숨기는 함수입니다.
    private fun hideAllLayouts() {
        linearLayout9.visibility = View.GONE
        linearLayout10.visibility = View.GONE
        linearLayout11.visibility = View.GONE
    }
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }

    fun onBackButtonClicked(view: View) {
        finish()
    }
    private fun uploadImagesAndGetUrls(imageUrls: List<Uri>, collectionTitle: String) {
        val storage = FirebaseStorage.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email

        // 기존 이미지의 URL을 가져옴
        val collectionData = intent.getSerializableExtra("collectionData") as? CollectionData
        val existingImageUrls = collectionData?.imageUrls ?: mutableListOf()

        val imageUrlsList = mutableListOf<String>()

        var num = 0
        if (imageUrls.size != 0) num = collectionData!!.imageUrls.size
        for (i in imageUrls) {
            val imgFileName = "$num.jpg"
            val imageRef = storage.reference.child("$email").child("$collectionTitle").child("$imgFileName")
            num++
            Image_num++

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
    private fun saveDataToFirestore(data: HashMap<String, Any>) {
        // Firestore에 데이터 저장
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email
        val collectionTitle = data["title"] as? String

        if (email != null && collectionTitle != null) {
            val documentPath = "$email" + "_collection/$collectionTitle"

            // 문서를 업데이트합니다.
            db.document(documentPath)
                .set(data)
                .addOnSuccessListener {
                    // 성공할 경우
                    Toast.makeText(this, "데이터가 업데이트되었습니다", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ModifyCollectionActivity", "Error updating document: $exception")
                }
        } else {
            // email이나 collectionTitle이 null인 경우에 대한 처리
            Log.w("ModifyCollectionActivity", "Error: email or collectionTitle is null")
            Toast.makeText(this, "데이터 업데이트 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }


}