package com.example.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OptionAdapter(/*private val options: List<String>*/) : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {

    // ViewHolder 클래스 정의
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder의 뷰 요소들을 선언
        // 예: val optionTextView = itemView.optionTextView
    }

    // onCreateViewHolder 메서드: ViewHolder를 생성하고 뷰를 연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.option_list, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder 메서드: ViewHolder의 뷰 요소에 데이터를 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    //val option = options[position]
        // ViewHolder의 뷰 요소에 데이터를 연결
        // 예: holder.optionTextView.text = option
    }

    // getItemCount 메서드: 데이터의 개수를 반환
    override fun getItemCount(): Int {
        return 0
    //return options.size
    }
}