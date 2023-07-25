package com.example.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class recyclerAdapter_calendar_d (val tempMonth:Int, val dayList: MutableList<Date>): RecyclerView.Adapter<recyclerAdapter_calendar_d.DayView>() {
    val ROW = 6

    inner class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_d, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.layout.findViewById<TextView>(R.id.tv_home_d).setOnClickListener {
            //날짜 클릭 (수정/삭제 가능)
            Toast.makeText(holder.layout.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
        }
        holder.layout.findViewById<TextView>(R.id.tv_home_d).text = dayList[position].date.toString()


        if(tempMonth != dayList[position].month) {
            holder.layout.findViewById<TextView>(R.id.tv_home_d).alpha = 0.2f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }

}