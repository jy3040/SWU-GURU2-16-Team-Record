package com.example.nota

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class write_collection : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.write_collection, container, false)

        // 카테고리 선택 Spinner 설정 (생략)

        // 사진 리사이클러뷰 설정
        val recyclerViewImage = view.findViewById<RecyclerView>(R.id.recyclerView_image)
        recyclerViewImage.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ImageAdapter() // 아래에서 ImageAdapter 구현한 부분
        }

        // 선택 추가 옵션 리사이클러뷰 설정
        /*
        val recyclerViewOption = view.findViewById<RecyclerView>(R.id.recyclerView_Option)
        recyclerViewOption.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OptionAdapter() // 아래에서 OptionAdapter 구현한 부분
        }
        */

        // 선택 추가 옵션 추가버튼 클릭 리스너 설정 (생략)

        return view
    }
}