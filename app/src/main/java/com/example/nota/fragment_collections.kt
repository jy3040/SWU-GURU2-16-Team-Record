package com.example.nota

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_collections.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_collections : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rv_collections_list:RecyclerView
    private lateinit var CollectionAdapter:CollectionAdapter
    private val existingCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_collections, container, false)

        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db?.collection("user")?.document("$uid")
        var email:String

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""

            val db = FirebaseFirestore.getInstance()
            val chipGroup: ChipGroup = view.findViewById(R.id.cp_collections_chip_group)


            FirebaseFirestore.getInstance()
                .collection("$userEmail" + "_collection")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val category = document.getString("category")
                        if (category != null && !existingCategories.contains(category)) {
                            existingCategories.add(category)
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
                    Log.w(TAG, "Error getting documents: $exception")
                }
        }

        // 리사이클러뷰 초기화
        rv_collections_list = view.findViewById(R.id.rv_collections_list)
        rv_collections_list.layoutManager = GridLayoutManager(requireContext(), 3)

        collectionRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email = documentSnapshot.getString("email").toString()
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

                                // CollectionData 객체를 생성하여 리스트에 추가
                                collectionList.add(CollectionData(email,imageUrls, title, rating,Y, M, D, category, content, optioncontent, optioncontent2, optioncontent3, optiontitle, optiontitle2, optiontitle3))
                            }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_collections.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_collections().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}