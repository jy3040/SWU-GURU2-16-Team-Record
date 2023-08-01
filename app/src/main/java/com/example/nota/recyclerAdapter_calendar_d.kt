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

    val ROW = 6

    inner class DayView(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_d, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        val dateTextView = holder.layout.findViewById<TextView>(R.id.tv_home_d)
        val dotView = holder.layout.findViewById<View>(R.id.v_home_dot)
        dateTextView.text = dayList[position].date.toString()

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayList[position])

        if (tempMonth != dayList[position].month) {
            dateTextView.alpha = 0.2f
        } else {
            dateTextView.alpha = 1.0f
        }

        if (currentDate in emailDatesList) {
            // 이메일 날짜 목록에 있는 날짜에 점을 표시합니다.
            dotView.setBackgroundResource(R.drawable.baseline_circle_24)
        } else {
            // 이메일 날짜 목록에 없는 날짜에는 점을 표시하지 않습니다.
            dotView.setBackgroundResource(0) // 배경 제거
        }

        dateTextView.setOnClickListener {
            // 날짜 클릭 (수정/삭제 가능)
            val clickedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayList[position])
            // 원하는 작업 수행 (예: 해당 날짜를 Toast로 표시)
            // Toast.makeText(holder.layout.context, clickedDate, Toast.LENGTH_SHORT).show()
        }
        Log.d("CalendarDebug", "currentDate: $currentDate")
        Log.d("CalendarDebug", "emailDatesList: $emailDatesList")
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }

}