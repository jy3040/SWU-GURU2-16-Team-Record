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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.reports, container, false)

        barChart = view.findViewById(R.id.bar_chart)
        pieChart = view.findViewById(R.id.pie_chart)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""
            drawPieChart(userEmail)

            // Query Firestore to get the documents that meet the specified condition
            val db = FirebaseFirestore.getInstance()
            val dataList: ArrayList<BarEntry> = ArrayList()
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
            val startMonth = currentMonth - 5

            for (i in startMonth..currentMonth) {
                db.collection("$userEmail" + "_collection")
                    .whereEqualTo("M", i)
                    .get()
                    .addOnSuccessListener { documents ->
                        val count = documents.size().toFloat()
                        dataList.add(BarEntry(i.toFloat(), count))

                        if (i == currentMonth) {
                            val barDataSet = BarDataSet(dataList, "NOTA")
                            barDataSet.color = Color.parseColor("#655691")
                            barDataSet.valueFormatter = NaturalValueFormatter()
                            val barData = BarData(barDataSet)
                            barChart.data = barData

                            val xAxis = barChart.xAxis
                            xAxis.valueFormatter = IndexAxisValueFormatter(
                                getXAxisLabels(
                                    startMonth - 2,
                                    currentMonth + 2
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
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting documents: ", exception)
                    }
            }

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

    class NaturalValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            // 값(value)을 정수로 변환하여 리턴합니다.
            return value.toInt().toString()
        }
    }

    class YAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            // Format the Y-axis label here (e.g., convert the float value to a desired format)
            return value.toInt().toString()
        }
    }

    private fun getXAxisLabels(startMonth: Int, endMonth: Int): List<String> {
        val xAxisLabels: MutableList<String> = ArrayList()
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        for (i in startMonth..endMonth) {
            val finalMonth = if (i < 1) i + 12 else i
            xAxisLabels.add(months[(finalMonth - 1) % 12]) // Wrap months around using modulo
        }

        return xAxisLabels
    }

    private fun getCountsByCategory(
        userEmail: String,
        topCategory1TextView: TextView,
        topCategory2TextView: TextView,
        topCategory3TextView: TextView,
        topCategory1count: TextView,
        topCategory2count: TextView,
        topCategory3count: TextView
    ) {
        // Map to hold the count for each category
        val categoryCountMap: MutableMap<String, Int> = mutableMapOf()

        // Query Firestore to get the documents and count the occurrences of each category
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

                // Sort the category count map in descending order
                val sortedCategoryCountList =
                    categoryCountMap.toList().sortedByDescending { (_, count) -> count }

                // Show the top 3 categories and their counts in the TextViews
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

                // Sort the category count map in descending order
                val sortedCategoryCountList =
                    categoryCountMap.toList().sortedByDescending { (_, count) -> count }

                // Prepare data for the pie chart
                val pieEntries: ArrayList<PieEntry> = ArrayList()
                val maxCategoriesToShow = 3 // 상위 3개 카테고리를 표시할 수
                var otherCount = 0 // 기타 카테고리의 개수를 세기 위한 변수

                // Add the top 3 categories to the pie chart data
                for (i in 0 until minOf(maxCategoriesToShow, sortedCategoryCountList.size)) {
                    val (category, count) = sortedCategoryCountList[i]
                    pieEntries.add(PieEntry(count.toFloat(), category))
                }

                // Calculate the count for the "기타" category
                for (i in maxCategoriesToShow until sortedCategoryCountList.size) {
                    otherCount += sortedCategoryCountList[i].second
                }

                // Add the "기타" category to the pie chart data if there are any
                if (otherCount > 0) {
                    pieEntries.add(PieEntry(otherCount.toFloat(), "기타"))
                }

                // Calculate the total count for all categories
                val totalCount = pieEntries.sumBy { it.value.toInt() }.toFloat()

                // Create a PieDataSet
                val pieDataSet = PieDataSet(pieEntries, "Categories")
                pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                pieDataSet.valueFormatter = PercentageValueFormatter(totalCount)

                // Create a PieData object and set it to the pie chart
                val pieData = PieData(pieDataSet)
                pieChart.data = pieData

                // Customize the pie chart
                pieChart.description.text = ""
                pieChart.setUsePercentValues(true)
                pieChart.animateY(2000)
                pieChart.setDrawEntryLabels(false)

                // Refresh the chart
                pieChart.invalidate()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }


    class PercentageValueFormatter(private val totalCount: Float) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val percentage = (value / totalCount) * 10
            return "${"%.1f".format(percentage)}%" // 소수점 첫째 자리까지 표시
        }
    }
}