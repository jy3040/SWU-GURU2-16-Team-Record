package com.example.nota

import android.content.ContentValues.TAG
import android.content.Intent
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

class fragment_collections : Fragment() {

    private lateinit var rv_collections_list:RecyclerView
    private lateinit var CollectionAdapter:CollectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collections, container, false)

        val tv_collections_title = view.findViewById<TextView>(R.id.tv_collections_title)
        val tv_collections_count = view.findViewById<TextView>(R.id.tv_collections_count)

        // Firebase 인증 객체를 가져옴
        val auth = FirebaseAuth.getInstance()
        // 현재 사용자의 UID를 가져옴
        val uid = auth.uid
        // Firestore 데이터베이스 객체를 가져옴
        val db = FirebaseFirestore.getInstance()
        // Firestore에서 사용자의 컬렉션 데이터를 가져올 Document 참조를 생성
        val collectionRef = db?.collection("user")?.document("$uid")
        // 사용자 이메일을 저장할 변수를 선언
        var email:String

        // 리사이클러뷰 초기화
        rv_collections_list = view.findViewById(R.id.rv_collections_list)
        rv_collections_list.layoutManager = GridLayoutManager(requireContext(), 3)

        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Firestore에서 사용자의 이메일을 가져옴.
                    email = documentSnapshot.getString("email").toString()

                    // Firestore에서 해당 사용자의 컬렉션 데이터를 가져옴.
                    db.collection("$email" + "_collection")
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            // 성공적으로 데이터를 가져온 경우
                            val collectionList = mutableListOf<CollectionData>()

                            for (document in querySnapshot) {
                                // 문서 데이터를 CollectionData 객체로 변환하여 리스트에 추가
                                val imageUrls =
                                    document.get("images") as? List<String> ?: emptyList()
                                val title = document.getString("title") ?: ""
                                val rating = document.getLong("rating") ?: 0L
                                val D = document.getLong("D") ?: 0L
                                val M = document.getLong("M") ?: 0L
                                val Y = document.getLong("Y") ?: 0L
                                val category = document.getString("category") ?: ""
                                val content = document.getString("content") ?: ""
                                val optioncontent = document.getString("optioncontent") ?: ""
                                val optioncontent2 = document.getString("optioncontent2") ?: ""
                                val optioncontent3 = document.getString("optioncontent3") ?: ""
                                val optiontitle = document.getString("optiontitle") ?: ""
                                val optiontitle2 = document.getString("optiontitle2") ?: ""
                                val optiontitle3 = document.getString("optiontitle3") ?: ""

                                collectionList.add(CollectionData(email,imageUrls, title, rating,Y, M, D, category, content, optioncontent, optioncontent2, optioncontent3, optiontitle, optiontitle2, optiontitle3))

                            }
                            tv_collections_count.text = "("+collectionList.size+")"
                            // 어댑터를 생성하고 리사이클러뷰에 연결
                            CollectionAdapter = CollectionAdapter(collectionList)
                            rv_collections_list.adapter = CollectionAdapter

                            CollectionAdapter.setOnItemClickListener(object : CollectionAdapter.OnItemClickListener {
                                override fun onItemClick(collectionData: CollectionData) {
                                    // CollectionData 객체를 이용하여 CollectionDetailActivity로 이동하는 Intent 작성
                                    val intent = Intent(requireContext(), CollectionDetailActivity::class.java)
                                    intent.putExtra("collectionData",collectionData)
                                    startActivity(intent)
                                }
                            })
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