package com.example.nota

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nota.BindingAdapter.loadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class fragment_setting: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.setting, container, false)

        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val myPage_name = view.findViewById<TextView>(R.id.myPage_name)

        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("user")

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    myPage_name.text = name
                    loadImage(profileImage,email.toString())
                }
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
            }

        val logoutButton = view.findViewById<Button>(R.id.button_logout)
        logoutButton.setOnClickListener {
            logoutUser()
        }
        return view
    }
    private fun getBitmapFromURL(src: String): Bitmap? {
        var inputStream: InputStream? = null
        try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            inputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            inputStream?.close()
        }
    }
    private fun logoutUser() {

        // 자동 로그인 설정에서 로그인 정보 삭제
        val sharedPreferences = requireContext().getSharedPreferences("login_status", 0)
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.apply()

        // 로그인 화면으로 이동
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}