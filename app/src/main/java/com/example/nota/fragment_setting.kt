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
        val button_setprofile = view.findViewById<Button>(R.id.button_setprofile)
        val collection_num = view.findViewById<TextView>(R.id.collection_num)
        val wishes_num = view.findViewById<TextView>(R.id.wishes_num)

        button_setprofile.setOnClickListener{
            val intent = Intent(requireContext(), FixActivity::class.java)
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth?.uid

        val collectionRef = db.collection("user").document("$uid")
        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val email = documentSnapshot.getString("email").toString()
                    val ncRef = db.collection("$email" + "_collection")

                    ncRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            val collectionCount = querySnapshot.size()
                            collection_num.text=collectionCount.toString()
                        }
                        .addOnFailureListener { exception ->
                            // 실패 시 동작할 코드를 작성합니다.
                        }
                    val nwRef = db.collection("$email"+"_wish")
                    nwRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            val wishCount = querySnapshot.size()
                            wishes_num.text=wishCount.toString()
                        }
                        .addOnFailureListener { exception ->

                        }
                }
            }

        collectionRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    val email = documentSnapshot.getString("email")
                    myPage_name.text = name

                    // 이미지를 직접 가져와서 profileImage에 설정
                    loadImage(profileImage,email.toString())
                } else {
                    // 문서가 존재하지 않을 경우에 대한 처리
                    Toast.makeText(requireContext(), "해당 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
                Toast.makeText(requireContext(), "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
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