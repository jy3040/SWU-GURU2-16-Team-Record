package com.example.nota

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import android.graphics.Color
import android.widget.EditText
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter


class fragment_report : Fragment() {

    lateinit var barChart: BarChart
    lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.reports, container, false)

        // 뷰에서 막대 차트와 파이 차트를 찾아서 변수에 할당
        barChart = view.findViewById(R.id.bar_chart)
        pieChart = view.findViewById(R.id.pie_chart)

        // 현재 사용자를 가져와서 사용자 이메일을 가져옴
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""
            // 파이 차트를 그리는 메서드를 호출하면서 사용자 이메일을 전달
            drawPieChart(userEmail)

            // Firestore에서 특정 조건을 만족하는 문서들을 가져옴
            val db = FirebaseFirestore.getInstance()
            val dataList: ArrayList<BarEntry> = ArrayList()
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
            val startMonth = currentMonth - 5

            for (i in startMonth..currentMonth) {
                db.collection("$userEmail" + "_collection")
                    .whereEqualTo("M", i)
                    .get()
                    .addOnSuccessListener { documents ->
                        // 해당 월의 데이터 개수를 가져와서 dataList에 추가
                        val count = documents.size().toFloat()
                        dataList.add(BarEntry(i.toFloat(), count))

                        if (i == currentMonth) {
                            val barDataSet = BarDataSet(dataList, "Month")
                            barDataSet.color = Color.parseColor("#8274AC")
                            barDataSet.valueFormatter = NaturalValueFormatter()
                            val barData = BarData(barDataSet)
                            barChart.data = barData

                            val xAxis = barChart.xAxis
                            xAxis.valueFormatter = IndexAxisValueFormatter(
                                getXAxisLabels(
                                    startMonth - 3,
                                    currentMonth
                                )
                            )

                            val yAxis = barChart.axisLeft
                            yAxis.valueFormatter = YAxisValueFormatter()
                            val rightYAxis = barChart.axisRight
                            rightYAxis.isEnabled = false

                            barChart.invalidate()
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.labelCount = 6

                            barChart.setFitBars(true)
                            barChart.description.text = ""
                            barChart.animateY(2000)
                            barChart.setDrawGridBackground(false)

                            barChart.axisLeft.isEnabled = false
                            barChart.xAxis.setDrawGridLines(false)
                            barChart.axisRight.setDrawGridLines(false)
                        }
                    }

                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting documents: ", exception)
                    }
            }

            // 사용자의 카테고리별 데이터를 가져와서 상위 3개 카테고리와 해당 카테고리의 개수를 표시
            getCountsByCategory(
                userEmail,
                view.findViewById(R.id.firstCategory),
                view.findViewById(R.id.secondCategory),
                view.findViewById(R.id.thirdCategory),
                view.findViewById(R.id.firstCategory_count),
                view.findViewById(R.id.secondCategory_count),
                view.findViewById(R.id.thirdCategory_count)
            )
        }

        return view
    }

    // 막대 차트의 값 포맷을 정수로 변환하는 클래스
    class NaturalValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    // y축 레이블 포맷을 정수로 변환하는 클래스
    class YAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    // 막대 차트의 x축 레이블을 생성하는 메서드
    private fun getXAxisLabels(startMonth: Int, endMonth: Int): List<String> {
        val xAxisLabels: MutableList<String> = ArrayList()
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        for (i in startMonth..endMonth) {
            val finalMonth = if (i < 1) i + 12 else i
            xAxisLabels.add(months[(finalMonth - 1) % 12]) // 월을 1부터 12까지 순환하여 표시
        }

        return xAxisLabels
    }

    // 사용자의 카테고리별 데이터를 가져와서 상위 3개 카테고리와 해당 카테고리의 개수를 텍스트 뷰에 표시하는 메서드
    private fun getCountsByCategory(
        userEmail: String,
        topCategory1TextView: TextView,
        topCategory2TextView: TextView,
        topCategory3TextView: TextView,
        topCategory1count: TextView,
        topCategory2count: TextView,
        topCategory3count: TextView
    ) {
        // 각 카테고리의 개수를 저장하기 위한 맵을 생성
        val categoryCountMap: MutableMap<String, Int> = mutableMapOf()

        // Firestore에서 사용자의 데이터를 가져와서 각 카테고리별로 개수를 계산
        FirebaseFirestore.getInstance()
            .collection("$userEmail" + "_collection")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val category = document.getString("category")
                    if (category != null) {
                        categoryCountMap[category] = (categoryCountMap[category] ?: 0) + 1
                    }
                }

                // 카테고리 개수를 기준으로 맵을 내림차순으로 정렬
                val sortedCategoryCountList =
                    categoryCountMap.toList().sortedByDescending { (_, count) -> count }

                // 상위 3개 카테고리와 해당 카테고리의 개수를 텍스트 뷰에 표시
                val topCategoriesText = StringBuilder()
                for (i in 0 until minOf(3, sortedCategoryCountList.size)) {
                    val (category, count) = sortedCategoryCountList[i]
                    when (i) {
                        0 -> {
                            // 상위 1번 카테고리
                            topCategory1TextView.text = category
                            topCategory1count.text = count.toString()
                        }

                        1 -> {
                            // 상위 2번 카테고리
                            topCategory2TextView.text = category
                            topCategory2count.text = count.toString()
                        }

                        2 -> {
                            // 상위 3번 카테고리
                            topCategory3TextView.text = category
                            topCategory3count.text = count.toString()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    // 사용자의 카테고리별 데이터를 가져와서 파이 차트를 그리는 메서드
    private fun drawPieChart(userEmail: String) {
        FirebaseFirestore.getInstance()
            .collection("$userEmail" + "_collection")
            .get()
            .addOnSuccessListener { documents ->
                val categoryCountMap: MutableMap<String, Int> = mutableMapOf()
                for (document in documents) {
                    val category = document.getString("category")
                    if (category != null) {
                        categoryCountMap[category] = (categoryCountMap[category] ?: 0) + 1
                    }
                }

                // 카테고리 개수를 기준으로 맵을 내림차순으로 정렬합니다.
                val sortedCategoryCountList =
                    categoryCountMap.toList().sortedByDescending { (_, count) -> count }

                val pieEntries: ArrayList<PieEntry> = ArrayList()
                val maxCategoriesToShow = 3 // 상위 3개 카테고리를 표시할 수
                var otherCount = 0 // 기타 카테고리의 개수를 세기 위한 변수

                // 상위 3개 카테고리를 파이 차트 데이터에 추가
                for (i in 0 until minOf(maxCategoriesToShow, sortedCategoryCountList.size)) {
                    val (category, count) = sortedCategoryCountList[i]
                    pieEntries.add(PieEntry(count.toFloat(), category))

                }
                // 나머지 카테고리의 개수를 계산
                for (i in maxCategoriesToShow until sortedCategoryCountList.size) {
                    otherCount += sortedCategoryCountList[i].second
                }

                // "기타" 카테고리를 파이 차트 데이터에 추가
                if (otherCount > 0) {
                    pieEntries.add(PieEntry(otherCount.toFloat(), "기타       1"))
                }

                // 모든 카테고리의 개수의 합을 계산
                val totalCount = pieEntries.sumBy { it.value.toInt() }.toFloat()

                // 파이 데이터셋을 생성
                val pieDataSet = PieDataSet(pieEntries, "Categories")

                // 커스텀 색상 리스트를 생성하여 파이 데이터셋에 설정
                val customColors = listOf(
                    Color.parseColor("#8274AC"),
                    Color.parseColor("#8E84AD"),
                    Color.parseColor("#CBB7D4"),
                    Color.parseColor("#C0A3D2")
                )
                pieDataSet.colors = customColors


                pieDataSet.valueFormatter = PercentageValueFormatter(totalCount)

                // 파이 데이터를 생성하고 파이 차트에 설정
                val pieData = PieData(pieDataSet)
                pieChart.data = pieData

                // 파이 차트를 커스텀
                pieChart.description.text = ""
                pieChart.setUsePercentValues(true)
                pieChart.animateY(2000)
                pieChart.setDrawEntryLabels(false)

                pieChart.invalidate()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }


    class PercentageValueFormatter(private val totalCount: Float) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            // 주어진 값(value)을 전체 합(totalCount)으로 나누어 백분율을 계산
            val percentage = (value / totalCount) * 10
            return "${"%.1f".format(percentage)}%" // 소수점 첫째 자리까지 표시
        }
    }
}