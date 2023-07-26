package com.example.nota

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart

class chart_piechart : AppCompatActivity(){

    lateinit var pieChart:PieChart
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports)

        /*
        //유저 퍼센트 값
        pieChart = findViewById(R.id.piechart)
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        //차트 드래그 -> 차트 회전
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        //차트 색
        //pieChart.setTransparentCircleColor(Color.White)

        pieChart.legend.isEnabled=false
        //pieChart.setEntryLabelColor(Color.White)
        pieChart.setEntryLabelTextSize(12f)


*/

    }



}