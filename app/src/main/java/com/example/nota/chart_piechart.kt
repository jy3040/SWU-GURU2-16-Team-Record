package com.example.nota

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Arrays

class chart_piechart : AppCompatActivity(){

    lateinit var pieChart:PieChart
    lateinit var pieEntries:ArrayList<PieEntry>
    lateinit var label:String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports)


        pieChart = findViewById(R.id.piechart)

        initPieChart()
        showPieChart()



    }

    //차트 모양 수정 함수 -> showPieChart()보다 먼저 호출
    fun initPieChart()
    {
        //amount 대신 percentages 사용
        pieChart.setUsePercentValues(true)

        //왼쪽 하단 모서리 설명 레이블 제거, 디폴트 = true
        pieChart.getDescription().setEnabled(false)

        //사용자가 차트 rotation 회전 가능
        pieChart.setRotationEnabled(true)

        //차트 회전 시 마찰
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        //오른쪽에서 첫번째 항목 시작
        pieChart.setRotationAngle(0f)


        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)


        //hollow -> solid
        pieChart.holeRadius=0f
        pieChart.transparentCircleRadius=0f

    }

    fun showPieChart(){
        label = "type"

        //카테고리 이름-개수를 쌍으로 맵핑하여 저장
        //자바에서 typeAmountMap.put("A", 100); 형식으로 사용
        var typeAmountMap = HashMap<String, Int>()
        typeAmountMap.put("A", 200)

        for(type in typeAmountMap.keys){
            pieEntries.add(PieEntry(typeAmountMap.get(type)!!.toFloat(), type))
        }
        //pieChartColorArray.xml의 컬러 배열을 받아와서
        //var colorArray:IntArray = Arrays.copyOfRange(getResources().getIntArray(R.array.pieChartColorArray),0,typeAmountMap.size)
        var colors = ArrayList<Int>()

        /*
        for(color in colorArray){
            // colorArray의 리스트를 colors 배열에 할당
            /*
            자바에서 활용법
            for(int color:colorArray
            { colors.add(color);}
             */
            colors.add(color)
        }
        //colors.add(Color.parseColor("#E5DFE8"))
        */


        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize=12f
        pieDataSet.setColors(colors)

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)

        pieChart.setData(pieData)
        pieChart.invalidate()

    }



}