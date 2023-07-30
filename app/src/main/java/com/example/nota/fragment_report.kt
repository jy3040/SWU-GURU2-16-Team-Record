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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter


class fragment_report : Fragment() {

    lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.reports, container, false)

        barChart = view.findViewById(R.id.bar_chart)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email

            // Access Firestore instance
            val db = FirebaseFirestore.getInstance()

            // Calculate the start and end months for the chart data
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // 현재 월을 가져옵니다.
            val startMonth = currentMonth - 5 // 현재로부터 5개월 전의 월을 계산합니다.

            // List to hold the BarEntry data
            val dataList: ArrayList<BarEntry> = ArrayList()

            // Query Firestore to get the documents that meet the specified condition
            for (i in startMonth..currentMonth) {
                db.collection("$userEmail" + "_collection")
                    .whereEqualTo("M", i)
                    .get()
                    .addOnSuccessListener { documents ->
                        // Count the number of documents with matching 'M' field value
                        val count = documents.size().toFloat()

                        // Add BarEntry with calculated x and y values to the dataList
                        dataList.add(BarEntry(i.toFloat(), count))

                        // If this is the last iteration, update the chart
                        if (i == currentMonth) {
                            // Create the BarDataSet and apply styling
                            val barDataSet = BarDataSet(dataList, "List")
                            barDataSet.color = Color.parseColor("#655691")

                            barDataSet.valueFormatter = NaturalValueFormatter()

                            // Create the BarData and set it to the chart
                            val barData = BarData(barDataSet)
                            barChart.data = barData

                            // Set the X-axis value formatter
                            val xAxis = barChart.xAxis
                            xAxis.valueFormatter = IndexAxisValueFormatter(getXAxisLabels(startMonth - 2, currentMonth +2))

                            // Set the Y-axis value formatter
                            val yAxis = barChart.axisLeft
                            yAxis.valueFormatter = YAxisValueFormatter()

                            val rightYAxis = barChart.axisRight
                            rightYAxis.isEnabled = false

                            // Update the chart
                            barChart.invalidate()
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.labelCount = 6

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting documents: ", exception)
                    }
            }
        }

        barChart.setFitBars(true)
        barChart.description.text = "Bar Chart"
        barChart.animateY(2000)

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

}
