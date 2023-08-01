package com.example.nota

import android.app.Activity
import android.content.ContentValues.TAG
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class CreateAccountActivity : AppCompatActivity() {

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
    private var currentImageUrl: Uri? = null

    // Firebase 관련 인스턴스들
    private lateinit var auth: FirebaseAuth
    private var fbStorage: FirebaseStorage? = null
    private var fbFirestore: FirebaseFirestore? = null

    // 액티비티가 생성될 때 호출되는 메서드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

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

        // "프로필 이미지 선택" 버튼 클릭 시 갤러리 열기
        button.setOnClickListener {
            openGallery()
        }

        // "회원가입" 버튼 클릭 시 회원가입 로직 수행
        button_logIn.setOnClickListener {
            val email = editText_email.text.toString().trim()
            val password = editText_password.text.toString().trim()
            val password_Verify = editText_passwordVerify.text.toString().trim()

            // 비밀번호 확인 후, 회원가입 처리
            if (password.equals(password_Verify)) {
                ImageUpload(it) // 선택한 프로필 이미지를 Firebase Storage에 업로드
                createUser(email, password) // 사용자 정보를 Firebase Authentication에 생성
            } else {
                Toast.makeText(this, "비밀번호 확인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 사용자 정보 업로드 메서드
    private fun uploadInfo() {
        if (true) {
            val name = editText_name.text.toString().trim()
            val email = editText_email.text.toString().trim()
            val password = editText_password.text.toString().trim()

            // 사용자 정보 객체 생성
            var userInfo = User()
            userInfo.email = email
            userInfo.name = name
            userInfo.password = password

            // Firebase Firestore에 사용자 정보 업로드
            fbFirestore?.collection("user")?.document(auth?.uid.toString())?.set(userInfo)
        }
    }

    // 갤러리 열기 메서드
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("ActivityResult", "something wrong")
        }
    }

    // "뒤로가기" 버튼 클릭 시 호출되는 메서드
    fun onBackButtonClicked(view: View) {
        finish()
    }

    // 사용자 정보 생성 메서드
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    uploadInfo() // 사용자 정보 업로드 처리
                    var intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    // 후에 반응
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    // 실패 후 화면 반응?
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // 프로필 이미지 업로드 메서드
    private fun ImageUpload(view: View) {
        var email = editText_email.text.toString().trim()
        var imgFileName = "PROFILE_" + email + "_png"
        var storageRef =
            fbStorage?.reference?.child("images")?.child(imgFileName)

        // 이미지 업로드 처리
        if (currentImageUrl != null) {
            storageRef?.putFile(currentImageUrl!!)
                ?.addOnSuccessListener {
                    // 성공적으로 업로드되었을 때의 처리
                }
                ?.addOnFailureListener { exception ->
                    // 업로드 실패 시의 처리
                    Toast.makeText(
                        view.context,
                        "이미지 업로드 실패: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
