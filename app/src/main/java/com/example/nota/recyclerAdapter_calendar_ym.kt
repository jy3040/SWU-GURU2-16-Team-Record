package com.example.nota

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nota.R
import java.util.Calendar
import java.util.Date

class recyclerAdapter_calendar_ym : RecyclerView.Adapter<recyclerAdapter_calendar_ym.MonthView>() {
    // 무한 스크롤을 위해 중간 값을 설정
    val center = Int.MAX_VALUE / 2
    private var calendar = Calendar.getInstance()
    private val dateList: MutableList<String> = mutableListOf()
    private val emailDatesList: MutableList<String> = mutableListOf()

    inner class MonthView(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_ym, parent, false)
        return MonthView(view)
    }

    // 이메일 날짜 추가 함수
    fun addDate(Year: String, Month: String, Day: String) {
        if (Month.toInt() < 10) {
            if (Day.toInt() < 10) {
                emailDatesList.add("$Year-0$Month-0$Day")
            } else {
                emailDatesList.add("$Year-0$Month-$Day")
            }
        } else {
            emailDatesList.add("$Year-$Month-$Day")
        }
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        // 현재 날짜와 시간 설정
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // 무한 스크롤을 위해 포지션에 해당하는 월을 설정
        calendar.add(Calendar.MONTH, position - center)

        // 해당 월의 연도와 월을 표시
        holder.layout.findViewById<TextView>(R.id.tv_home_yearMonth).text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(
            Calendar.MONTH) + 1}월"

        // 해당 월의 첫 번째 날을 기준으로 달력에 표시될 일자 리스트 생성
        val tempMonth = calendar.get(Calendar.MONTH)
        var dayList: MutableList<Date> = MutableList(6 * 7) { Date() }
        for (i in 0..5) {
            for (k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        // 달력 일자 리스트를 표시할 리사이클러뷰와 어댑터 설정
        val dayListManager = GridLayoutManager(holder.layout.context, 7)
        val dayListAdapter = recyclerAdapter_calendar_d(tempMonth, dayList, emailDatesList)
        Log.d("CalendarDebug", "ym_emailDatesList: $emailDatesList")

        holder.layout.findViewById<RecyclerView>(R.id.rv_home_calendar_d).apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }

    // 무한 스크롤을 위해 아이템 개수를 매우 큰 값으로 설정
    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}