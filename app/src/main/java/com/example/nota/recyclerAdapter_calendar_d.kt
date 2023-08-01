package com.example.nota

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nota.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class recyclerAdapter_calendar_d(
    private val tempMonth: Int,
    private val dayList: MutableList<Date>,
    private val emailDatesList: List<String>
) : RecyclerView.Adapter<recyclerAdapter_calendar_d.DayView>() {

    // 달력의 행(Row) 개수
    val ROW = 6

    // 각 날짜를 표시하는 뷰 홀더 클래스를 정의하는 내부 클래스
    inner class DayView(val layout: View) : RecyclerView.ViewHolder(layout)

    // 뷰 홀더를 생성하고 레이아웃을 연결하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_d, parent, false)
        return DayView(view)
    }

    // 각 뷰 홀더에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: DayView, position: Int) {
        val dateTextView = holder.layout.findViewById<TextView>(R.id.tv_home_d)
        val dotView = holder.layout.findViewById<View>(R.id.v_home_dot)
        dateTextView.text = dayList[position].date.toString()

        // 현재 날짜를 "yyyy-MM-dd" 형식으로 변환하여 저장
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayList[position])

        // 다른 달의 날짜는 투명도를 줄여서 비활성화 처리
        if (tempMonth != dayList[position].month) {
            dateTextView.alpha = 0.2f
        } else {
            dateTextView.alpha = 1.0f
        }

        // 이메일 날짜 목록에 해당 날짜가 있는 경우 점을 표시
        if (currentDate in emailDatesList) {
            dotView.setBackgroundResource(R.drawable.baseline_circle_24)
        } else {
            // 이메일 날짜 목록에 없는 날짜에는 점을 표시하지 않음 (배경 제거)
            dotView.setBackgroundResource(0)
        }

        // 날짜를 클릭했을 때 처리하는 이벤트 리스너
        dateTextView.setOnClickListener {
            // 원하는 작업 수행 (예: 해당 날짜를 Toast로 표시)
            // Toast.makeText(holder.layout.context, currentDate, Toast.LENGTH_SHORT).show()
        }

        // 디버그용 로그 메시지 출력
        Log.d("CalendarDebug", "currentDate: $currentDate")
        Log.d("CalendarDebug", "emailDatesList: $emailDatesList")
    }

    // 뷰 홀더 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return ROW * 7
    }
}