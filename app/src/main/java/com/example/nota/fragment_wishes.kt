package com.example.nota

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class fragment_wishes : Fragment() {

    private lateinit var rv_wishes_list: RecyclerView
    private lateinit var WishAdapter: WishAdapter
    private val existingW_Category = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Fragment 생성 시 전달받은 인자를 처리할 필요가 있을 경우 여기에서 처리합니다.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wishes, container, false)

        val tv_wishes_count = view.findViewById<TextView>(R.id.tv_wishes_count)

        // Firebase 인스턴스 가져오기
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db?.collection("user")?.document("$uid")
        var email: String

        // 사용자의 위시 리스트를 가져와서 화면에 표시

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""

            val db = FirebaseFirestore.getInstance()
            val chipGroup: ChipGroup = view.findViewById(R.id.cp_wishes_chip_group)


            FirebaseFirestore.getInstance()
                .collection("$userEmail" + "_wish")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val category = document.getString("category")
                        if (category != null && !existingW_Category.contains(category)) {
                            existingW_Category.add(category)
                            // 카테고리 리스트에 새로운 카테고리가 있으면, 해당 카테고리로 Chip을 동적으로 생성하여 ChipGroup에 추가
                            val chip = Chip(requireContext())
                            chip.text = category
                            // Chip의 스타일과 속성을 설정하는 코드 추가
                            // chip.setStyle(...);
                            // chip.setChipBackgroundColorResource(...);
                            // chip.setTextColor(...);
                            // chip.setChipStrokeColorResource(...);
                            // chip.setChipStrokeWidth(...);
                            chipGroup.addView(chip)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패한 경우
                    Log.w(ContentValues.TAG, "Error getting documents: $exception")
                }
        }
        // 리사이클러뷰 초기화
        rv_wishes_list = view.findViewById(R.id.rv_wishes_list)

        // 리사이클러뷰에 사용할 레이아웃 매니저 설정
        val layoutManager = GridLayoutManager(requireContext(), 1)
        rv_wishes_list.layoutManager = layoutManager


        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email = documentSnapshot.getString("email").toString()
                    db.collection("$email" + "_wish")
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            // 성공적으로 데이터를 가져온 경우
                            val collectionList = mutableListOf<WishData>()

                            for (document in querySnapshot) {
                                // 문서 데이터를 WishData 객체로 변환하여 리스트에 추가
                                val title = document.getString("title") ?: ""
                                val category = document.getString("category") ?: ""
                                val content = document.getString("content") ?: ""
                                val checked = document.getBoolean("checked")!!

                                // WishData 객체를 생성하여 리스트에 추가
                                collectionList.add(WishData(email, category, content, title, checked))
                            }
                            tv_wishes_count.text="("+collectionList.size+")"
                            // 어댑터를 생성하고 리사이클러뷰에 연결
                            WishAdapter = WishAdapter(collectionList)
                            rv_wishes_list.adapter = WishAdapter
                        }
                        .addOnFailureListener { exception ->
                            // 실패한 경우
                            Log.w("Firestore", "Error getting documents: $exception")
                        }
                }
            }

        return view
    }
}