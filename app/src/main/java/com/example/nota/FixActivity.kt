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

    lateinit var editText_name: EditText
    lateinit var editText_email: EditText
    lateinit var editText_password: EditText
    lateinit var editText_passwordVerify: EditText
    lateinit var button_logIn: Button

    lateinit var profileImage: ImageView
    lateinit var button: Button

    private var currentImageUrl: Uri? = null

    private lateinit var auth: FirebaseAuth
    private var fbStorage: FirebaseStorage? = null
    private var fbFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fix_person)

        editText_name = findViewById(R.id.editText_name)
        editText_email = findViewById(R.id.editText_email)
        editText_password = findViewById(R.id.editText_password)
        editText_passwordVerify = findViewById(R.id.editText_passwordVerify)
        button_logIn = findViewById(R.id.button_logIn)
        button = findViewById(R.id.button)
        profileImage = findViewById(R.id.profileImage)

        auth = FirebaseAuth.getInstance()
        fbStorage = FirebaseStorage.getInstance()
        fbFirestore = FirebaseFirestore.getInstance()

        val uid = auth?.uid

        val collectionRef = fbFirestore?.collection("user")?.document("$uid")

        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    val email = documentSnapshot.getString("email")
                    editText_name.setText(name)

                    // 이미지를 직접 가져와서 profileImage에 설정
                    loadImage(profileImage,email.toString())
                } else {
                    // 문서가 존재하지 않을 경우에 대한 처리
                    Toast.makeText(this, "해당 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
                Toast.makeText(this, "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        button.setOnClickListener {
            openGallery()
        }



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
                                        updateInfo()
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

    fun onBackButtonClicked(view: View) {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY) {
                currentImageUrl = data?.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                    profileImage.setImageBitmap(bitmap)

                    ImageUpload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("ActivityResult", "something wrong")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    private fun updateInfo() {
        val uid = auth?.uid
        val washingtonRef = fbFirestore?.collection("user")?.document("$uid")

// Set the "isCapital" field of the city 'DC'
        washingtonRef
            ?.update("name", editText_name.text.toString())
            ?.addOnSuccessListener {  }
            ?.addOnFailureListener {  }
        washingtonRef
            ?.update("password", editText_passwordVerify.text.toString())
            ?.addOnSuccessListener {  }
            ?.addOnFailureListener {  }

    }

    private fun ImageUpload() {
        val uid = auth?.uid

        val collectionRef = fbFirestore?.collection("user")?.document("$uid")
        var email:String

        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email = documentSnapshot.getString("email").toString()

                    isImageExists(email) { exists ->
                        if (exists) {
                            ImageDelete(email)
                        }
                    }
                    var imgFileName = "PROFILE_${email}_png"
                    var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

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
            }
            ?.addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
                Toast.makeText(this, "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }



    }

    private fun ImageDelete(email: String) {
        var imgFileName = "PROFILE_" + email + "_png"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)


        if (currentImageUrl != null) {
            storageRef?.delete()?.addOnSuccessListener { // File deleted successfully
            }?.addOnFailureListener {
                Toast.makeText(this@FixActivity, "이미지 삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun isImageExists(email: String, onComplete: (Boolean) -> Unit) {
        val imgFileName = "PROFILE_${email}_png"
        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            // 다운로드 URL을 가져오는데 성공하면 해당 파일이 존재하는 것으로 간주
            onComplete(true)
        }?.addOnFailureListener { exception ->
            // 다운로드 URL을 가져오는데 실패하면 해당 파일이 존재하지 않는 것으로 간주
            onComplete(false)
        }
    }
}