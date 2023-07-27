package com.example.nota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class CreateAccountActivity : AppCompatActivity() {

    lateinit var editText_name: EditText
    lateinit var editText_password: EditText
    lateinit var editText_passwordVerify: EditText
    lateinit var button_logIn: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        editText_name = findViewById(R.id.editText_name)
        editText_password = findViewById(R.id.editText_password)
        editText_passwordVerify = findViewById(R.id.editText_passwordVerify)
        button_logIn = findViewById(R.id.button_logIn)

        auth = FirebaseAuth.getInstance()



        button_logIn.setOnClickListener {
            val email = editText_name.text.toString().trim()
            val password = editText_password.text.toString().trim()
            val password_Verify = editText_passwordVerify.text.toString().trim()

            // Validate...
            if( password.equals(password_Verify)){
            createUser(email, password)
            }else{
                Toast.makeText(this, "비밀번호 확인 실패", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun onBackButtonClicked(view: View) {
        finish()
    }
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    var intent = Intent(this, LoginActivity:: class.java)
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
}