package com.example.nota

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class fragment_home: Fragment() {

    private lateinit var fb_home_floating:FloatingActionButton
    private lateinit var fb_home_floating_c:FloatingActionButton
    private lateinit var tv_home_floating_c:TextView
    private lateinit var fb_home_floating_w:FloatingActionButton
    private lateinit var tv_home_floating_w:TextView

    // 플로팅 액션 버튼 및 텍스트뷰를 보여줄지 여부를 나타내는 변수
    var isRunning:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 화면을 구성하는 뷰를 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Firebase와 관련된 객체들을 초기화합니다.
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth?.uid
        val fbFirestore = FirebaseFirestore.getInstance()

        // 달력 부분을 초기화합니다.
        //----------------------------------------
        val monthListManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = recyclerAdapter_calendar_ym()

        val emailRef = db.collection("user").document("$uid")
        emailRef.get().addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot.exists()){
                val email = documentSnapshot.getString("email")
                val emailCollectionRef = fbFirestore?.collection("$email"+"_collection")

                emailCollectionRef?.get()?.addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // 날짜 정보를 가져와서 필요한 형식으로 가공하고, recyclerAdapter_calendar_ym 어댑터에 전달
                        val Y = document.getLong("Y").toString()
                        val M = document.getLong("M").toString()
                        val D = document.getLong("D").toString()// 날짜 정보가 저장된 필드명으로 변경해야 함
                        monthListAdapter.addDate(Y,M,D)
                    }
                    // 데이터가 변경되었음을 어댑터에 알림
                    monthListAdapter.notifyDataSetChanged()
                }?.addOnFailureListener { exception ->
                    // 데이터를 가져오는 과정에서 오류가 발생한 경우에 대한 처리
                }
            }
        }
        val calendar_custom=view.findViewById<RecyclerView>(R.id.rv_home_calendar)

        calendar_custom.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE/2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(calendar_custom)

        //----------------------------------------

        // 사용자 이름을 뷰에 표시합니다.
        val tv_home_user = view.findViewById<TextView>(R.id.tv_home_user)

        val collectionRef = db.collection("user").document("$uid")

        collectionRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    tv_home_user.text = name

                } else {
                    // 문서가 존재하지 않을 경우에 대한 처리
                    Toast.makeText(requireContext(), "해당 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리하는 로직을 구현합니다.
                Toast.makeText(requireContext(), "데이터 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // 위시 리스트를 초기화합니다.
        var email:String
        val rv_home_wishes = view.findViewById<RecyclerView>(R.id.rv_home_wishes)
        var WishAdapter:WishAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        rv_home_wishes.layoutManager = layoutManager

        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email = documentSnapshot.getString("email").toString()
                    db.collection("$email" + "_wish")
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            // 성공적으로 데이터를 가져온 경우
                            val wishList = mutableListOf<WishData>()

                            for (document in querySnapshot) {
                                // 문서 데이터를 CollectionData 객체로 변환하여 리스트에 추가
                                val title = document.getString("title") ?: ""
                                val category = document.getString("category") ?: ""
                                val content = document.getString("content") ?: ""
                                val checked = document.getBoolean("checked")!!

                                if(checked == false) {
                                    // CollectionData 객체를 생성하여 리스트에 추가
                                    wishList.add(WishData(email, category, content, title, checked))
                                }
                            }

                            // 어댑터를 생성하고 리사이클러뷰에 연결
                            WishAdapter = WishAdapter(wishList)
                            rv_home_wishes.adapter = WishAdapter
                        }
                        .addOnFailureListener { exception ->
                            // 실패한 경우
                            Log.w("Firestore", "Error getting documents: $exception")
                        }
                }
            }

        fb_home_floating = view.findViewById(R.id.fb_home_floating)
        fb_home_floating_c = view.findViewById(R.id.fb_home_floating_c)
        tv_home_floating_c = view.findViewById(R.id.tv_home_floating_c)
        fb_home_floating_w = view.findViewById(R.id.fb_home_floating_w)
        tv_home_floating_w = view.findViewById(R.id.tv_home_floating_w)

        fb_home_floating.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) {
                // 다른 플로팅액션버튼들과 텍스트뷰들을 보이도록 설정
                fb_home_floating_c.visibility = View.VISIBLE
                tv_home_floating_c.visibility = View.VISIBLE
                fb_home_floating_w.visibility = View.VISIBLE
                tv_home_floating_w.visibility = View.VISIBLE
            } else {
                // 다른 플로팅액션버튼들과 텍스트뷰들을 숨기도록 설정
                fb_home_floating_c.visibility = View.GONE
                tv_home_floating_c.visibility = View.GONE
                fb_home_floating_w.visibility = View.GONE
                tv_home_floating_w.visibility = View.GONE
            }
        }
        // 컬렉션 작성 페이지로 이동
        fb_home_floating_c.setOnClickListener {
            val intent = Intent(requireContext(), WriteCollectionActivity::class.java)
            startActivity(intent)
        }
        // 위시 작성 페이지로 이동
        fb_home_floating_w.setOnClickListener {
            val intent = Intent(requireContext(), WriteWishActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}