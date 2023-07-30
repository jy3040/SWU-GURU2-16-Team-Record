package com.example.nota

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_home: Fragment() {

    private lateinit var fb_home_floating:FloatingActionButton
    private lateinit var fb_home_floating_c:FloatingActionButton
    private lateinit var tv_home_floating_c:TextView
    private lateinit var fb_home_floating_w:FloatingActionButton
    private lateinit var tv_home_floating_w:TextView

    var isRunning:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //달력
        //----------------------------------------
        val monthListManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = recyclerAdapter_calendar_ym()

        val calendar_custom=view.findViewById<RecyclerView>(R.id.rv_home_calendar)

        calendar_custom.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE/2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(calendar_custom)
        //----------------------------------------

        val tv_home_user = view.findViewById<TextView>(R.id.tv_home_user)
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth?.uid

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
        //----------------------------------------
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

                                // CollectionData 객체를 생성하여 리스트에 추가
                                wishList.add(WishData(email, category, content, title))
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


        //---------------------------------------



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
        fb_home_floating_c.setOnClickListener {
            val intent = Intent(requireContext(), WriteCollectionActivity::class.java)
            startActivity(intent)
        }
        fb_home_floating_w.setOnClickListener {
            val intent = Intent(requireContext(), WriteWishActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}