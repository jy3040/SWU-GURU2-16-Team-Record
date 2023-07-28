package com.example.nota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        val button2 = findViewById<View>(R.id.button2)
        val button3 = findViewById<View>(R.id.button3)

        button2.setOnClickListener {
            // 회원가입 페이지로 이동
            val intent = Intent(this@StartActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            // 로그인 페이지로 이동
            val intent = Intent(this@StartActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}