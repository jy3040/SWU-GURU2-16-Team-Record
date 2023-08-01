package com.example.nota

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.nota.BindingAdapter.loadImage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FixActivity : AppCompatActivity() {

    private val OPEN_GALLERY = 1

    // 사용자 입력 위젯들
    lateinit var editText_name: EditText
    lateinit var editText_email: EditText
    lateinit var editText_password: EditText
    lateinit var editText_passwordVerify: EditText
    lateinit var button_logIn: Button

    // 프로필 이미지 관련 위젯
    lateinit var profileImage: ImageView
    lateinit var button: Button

    // 현재 선택된 프로필 이미지의 Uri
    private var currentImageUrl: Uri? = null

    // Firebase 관련 인스턴스들
    private lateinit var auth: FirebaseAuth
    private var fbStorage: FirebaseStorage? = null
    private var fbFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fix_person)

        // 위젯들을 레이아웃과 연결
        editText_name = findViewById(R.id.editText_name)
        editText_email = findViewById(R.id.editText_email)
        editText_password = findViewById(R.id.editText_password)
        editText_passwordVerify = findViewById(R.id.editText_passwordVerify)
        button_logIn = findViewById(R.id.button_logIn)
        button = findViewById(R.id.button)
        profileImage = findViewById(R.id.profileImage)

        // Firebase 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        fbStorage = FirebaseStorage.getInstance()
        fbFirestore = FirebaseFirestore.getInstance()

        // 현재 로그인한 사용자의 uid를 가져와 해당 사용자 정보를 가져오기 위한 데이터베이스 레퍼런스 생성
        val uid = auth?.uid
        val collectionRef = fbFirestore?.collection("user")?.document("$uid")

        // Firestore에서 현재 사용자의 정보를 가져와 위젯에 설정
        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    val email = documentSnapshot.getString("email")
                    editText_name.setText(name)

                    // 프로필 이미지를 직접 가져와서 profileImage에 설정
                    loadImage(profileImage, email.toString())
                } else {
                    // 문서가 존재하지 않을 경우에 대한 처리
                    Toast.makeText(this, "해당 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
                Toast.makeText(this, "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // "프로필 이미지 선택" 버튼 클릭 시 갤러리 열기
        button.setOnClickListener {
            openGallery()
        }

        // "수정 완료" 버튼 클릭 시 사용자 정보 수정 처리
        button_logIn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val name: String = editText_name.text.toString()
                val email: String = editText_email.text.toString()
                val password: String = editText_password.text.toString()
                val Npassword: String = editText_passwordVerify.text.toString()

                val user = Firebase.auth.currentUser

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(
                        password
                    )
                ) {
                    Toast.makeText(this@FixActivity, "이메일과 비밀번호는 필수 입력사항 입니다.", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    this@FixActivity,
                    object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                user!!.updatePassword(Npassword).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        updateInfo() // 사용자 정보 수정 처리
                                        Toast.makeText(
                                            this@FixActivity,
                                            "개인정보 수정 성공",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                val intent = Intent(
                                    this@FixActivity,
                                    MainActivity::class.java
                                ).putExtra("userId", auth.uid)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@FixActivity,
                                    "이메일 또는 비밀번호가 다릅니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        })
    }

    // "뒤로가기" 버튼 클릭 시 호출되는 메서드
    fun onBackButtonClicked(view: View) {
        finish()
    }

    // 갤러리에서 이미지 선택 후 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY) {
                currentImageUrl = data?.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                    profileImage.setImageBitmap(bitmap)

                    ImageUpload() // 선택한 프로필 이미지를 Firebase Storage에 업로드
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("ActivityResult", "something wrong")
        }
    }

    // 갤러리 열기 메서드
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    // 사용자 정보 수정 메서드
    private fun updateInfo() {
        val uid = auth?.uid
        val washingtonRef = fbFirestore?.collection("user")?.document("$uid")

        // 사용자 정보 업데이트
        washingtonRef
            ?.update("name", editText_name.text.toString())
            ?.addOnSuccessListener {  }
            ?.addOnFailureListener {  }
        washingtonRef
            ?.update("password", editText_passwordVerify.text.toString())
            ?.addOnSuccessListener {  }
            ?.addOnFailureListener {  }

    }

    // 프로필 이미지 업로드 메서드
    private fun ImageUpload() {
        val uid = auth?.uid

        val collectionRef = fbFirestore?.collection("user")?.document("$uid")

        // Firestore에서 현재 사용자의 이메일 정보 가져오기
        collectionRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val email = documentSnapshot.getString("email").toString()

                // 기존에 저장된 프로필 이미지가 있을 경우 삭제
                isImageExists(email) { exists ->
                    if (exists) {
                        ImageDelete(email)
                    }
                }

                // 새로운 프로필 이미지를 Firebase Storage에 업로드
                val imgFileName = "PROFILE_${email}_png"
                val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

                if (currentImageUrl != null) {
                    storageRef?.putFile(currentImageUrl!!)
                        ?.addOnSuccessListener {
                            // 업로드 성공 시 아무것도 없음
                        }
                        ?.addOnFailureListener { exception ->
                            Toast.makeText(
                                this@FixActivity,
                                "이미지 업로드 실패: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                // 문서가 존재하지 않을 경우에 대한 처리
                Toast.makeText(this, "해당 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener { exception ->
            // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
            Toast.makeText(this, "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 프로필 이미지 삭제 메서드
    private fun ImageDelete(email: String) {
        val imgFileName = "PROFILE_" + email + "_png"
        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        // 이미지 파일 삭제
        if (currentImageUrl != null) {
            storageRef?.delete()?.addOnSuccessListener {
                // 이미지 파일 삭제 성공
            }?.addOnFailureListener {
                Toast.makeText(this@FixActivity, "이미지 삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 프로필 이미지 존재 여부 확인 메서드
    private fun isImageExists(email: String, onComplete: (Boolean) -> Unit) {
        val imgFileName = "PROFILE_${email}_png"
        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        // Firebase Storage에서 이미지 다운로드 URL을 가져와서 존재 여부 확인
        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            // 다운로드 URL을 가져오는데 성공하면 해당 파일이 존재하는 것으로 간주
            onComplete(true)
        }?.addOnFailureListener { exception ->
            // 다운로드 URL을 가져오는데 실패하면 해당 파일이 존재하지 않는 것으로 간주
            onComplete(false)
        }
    }
}