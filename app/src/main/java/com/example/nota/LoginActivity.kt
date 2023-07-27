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

class LoginActivity : AppCompatActivity() {
    lateinit var btn_login_login: Button
    lateinit var et_login_name: EditText
    lateinit var et_login_pw: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        et_login_name = findViewById(R.id.et_login_name)
        et_login_pw = findViewById(R.id.et_login_pw)
        btn_login_login = findViewById(R.id.btn_login_login)

        auth = FirebaseAuth.getInstance()

        btn_login_login.setOnClickListener(object: View.OnClickListener  {
            override fun onClick(p0: View?) {
                val email:String = et_login_name.text.toString()
                val pw:String = et_login_pw.text.toString()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pw)) {
                    Toast.makeText(
                        this@LoginActivity,
                        "이메일과 비밀번호는 필수 입력사항 입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this@LoginActivity,
                    object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    .putExtra("userId", auth.uid)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }

        })
    }

    }
z