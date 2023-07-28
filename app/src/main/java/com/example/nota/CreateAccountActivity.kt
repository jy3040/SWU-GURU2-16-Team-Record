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

    lateinit var editText_name: EditText
    lateinit var editText_email: EditText
    lateinit var editText_password: EditText
    lateinit var editText_passwordVerify: EditText
    lateinit var button_logIn: Button

    lateinit var profileImage: ImageView
    lateinit var button:Button

    private var currentImageUrl: Uri? = null

    private lateinit var auth: FirebaseAuth
    private var fbStorage: FirebaseStorage?=null
    private var fbFirestore: FirebaseFirestore?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

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

        // firestore에 user정보 uid와 email 저장


        button.setOnClickListener {
            openGallery()
        }


        button_logIn.setOnClickListener {
            val name = editText_name.text.toString().trim()
            val email = editText_email.text.toString().trim()
            val password = editText_password.text.toString().trim()
            val password_Verify = editText_passwordVerify.text.toString().trim()

            //Validate
            if( password.equals(password_Verify)){
                if(true){
                var userInfo = User()

                userInfo.uid = auth?.uid
                userInfo.email = auth?.currentUser?.email
                    userInfo.name = name
                    userInfo. password = password

                fbFirestore?.collection("user")?.document(auth?.uid.toString())?.set(userInfo)
            }

                ImageUpload(it)
                createUser(email, password)
            }else{
                Toast.makeText(this, "비밀번호 확인 실패", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }
    // 이미지 URL을 비트맵으로 저장 > currentImageUrl에 Url저장
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY) {
                currentImageUrl = data?.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                    profileImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("ActivityResult", "something wrong")
        }
    }
    fun onBackButtonClicked(view: View) {
        finish()
    }

    // 사용자 정보 만들기
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
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
    private fun ImageUpload(view: View){
        var email = editText_email.text.toString().trim()
        var imgFileName = "PROFILE_"+email+"_png"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        if (currentImageUrl !=null) {
            storageRef?.putFile(currentImageUrl!!)
                ?.addOnSuccessListener {
                }
                ?.addOnFailureListener { exception ->
                    Toast.makeText(
                        view.context,
                        "이미지 업로드 실패: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}