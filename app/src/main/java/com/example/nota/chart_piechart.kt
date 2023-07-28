package com.example.nota

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Arrays
import java.util.Calendar
import java.util.Calendar.MONTH

class chart_piechart : AppCompatActivity() {

    private val MAX_X_VALUE =13
    private val GROUPS=2
    private val GROUP_1_LABEL="Orders"
    private val BAR_SPACE=0.1f
    private val BAR_WIDTH=0.8f

    private var chart: BarChart?=null
    private var pieChart: PieChart?=null
    protected var tfRegular: Typeface?=null
    protected var tfLight: Typeface?=null

    private val statValueds: ArrayList<Int> = ArrayList()

    protected val statsTitles = arrayOf(
        "a", "b"
    )


    private val calendar: Calendar = Calendar.getInstance()
    private val month = calendar.get(Calendar.MONTH)


    /*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        chart = binding.barChart
        pieChart = binding.pieChart
        binding.barChartTitleTV.text = "${MONTH} Sales"
        getStats()
        return binding.root
    }
*/
    private fun preparePieData() {
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports)


    }
}