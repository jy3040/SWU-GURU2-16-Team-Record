package com.example.nota

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import recyclerAdapter_calendar_d
import java.util.Calendar
import java.util.Date

class recyclerAdapter_calendar_ym: RecyclerView.Adapter<recyclerAdapter_calendar_ym.MonthView>() {
    val center = Int.MAX_VALUE / 2
    private var calendar = Calendar.getInstance()
    private val dateList: MutableList<String> = mutableListOf()
    private val emailDatesList: MutableList<String> = mutableListOf()

    inner class MonthView(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_ym, parent, false)
        return MonthView(view)
    }
    fun addDate(Year: String, Month: String, Day: String) {
        if(Month.toInt()<10){
            if(Day.toInt()<10) {
                emailDatesList.add("$Year-0" + "$Month-0"+"$Day")
            }else{
                emailDatesList.add("$Year-0" + "$Month-$Day")
            }
        }else {
            emailDatesList.add("$Year-$Month-$Day")
        }
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, position - center)

        holder.layout.findViewById<TextView>(R.id.tv_home_yearMonth).text= "${calendar.get(Calendar.YEAR)}년 ${calendar.get(
            Calendar.MONTH) + 1}월"
        val tempMonth = calendar.get(Calendar.MONTH)

        var dayList: MutableList<Date> = MutableList(6 * 7) { Date() }
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        val dayListManager = GridLayoutManager(holder.layout.context, 7)
        val dayListAdapter = recyclerAdapter_calendar_d(tempMonth, dayList, emailDatesList)
        Log.d("CalendarDebug", "ym_emailDatesList: $emailDatesList")

        holder.layout.findViewById<RecyclerView>(R.id.rv_home_calendar_d).apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}