package com.example.nota

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_record.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_record : Fragment() {
    //탭바 화면 이동 구현
    private lateinit var viewPager_record: ViewPager2
    private lateinit var tabLayout_record: TabLayout

    private lateinit var fb_record_floating: FloatingActionButton
    private lateinit var fb_record_floating_c: FloatingActionButton
    private lateinit var tv_record_floating_c: TextView
    private lateinit var fb_record_floating_w: FloatingActionButton
    private lateinit var tv_record_floating_w: TextView

    var isRunning:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_record, container, false)

        //----------------------------------------------
        fb_record_floating = view.findViewById(R.id.fb_record_floating)
        fb_record_floating_c = view.findViewById(R.id.fb_record_floating_c)
        tv_record_floating_c = view.findViewById(R.id.tv_record_floating_c)
        fb_record_floating_w = view.findViewById(R.id.fb_record_floating_w)
        tv_record_floating_w = view.findViewById(R.id.tv_record_floating_w)

        fb_record_floating.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) {
                // 다른 플로팅액션버튼들과 텍스트뷰들을 보이도록 설정
                fb_record_floating_c.visibility = View.VISIBLE
                tv_record_floating_c.visibility = View.VISIBLE
                fb_record_floating_w.visibility = View.VISIBLE
                tv_record_floating_w.visibility = View.VISIBLE
            } else {
                // 다른 플로팅액션버튼들과 텍스트뷰들을 숨기도록 설정
                fb_record_floating_c.visibility = View.GONE
                tv_record_floating_c.visibility = View.GONE
                fb_record_floating_w.visibility = View.GONE
                tv_record_floating_w.visibility = View.GONE
            }
        }
        fb_record_floating_c.setOnClickListener {
            val intent = Intent(requireContext(), WriteCollectionActivity::class.java)
            startActivity(intent)
        }
        fb_record_floating_w.setOnClickListener {
            val intent = Intent(requireContext(), WriteWishActivity::class.java)
            startActivity(intent)
        }
    //탭바 화면 이동 구현
    //-------------------------------------------------------
        viewPager_record = view.findViewById(R.id.vp_record)
        tabLayout_record = view.findViewById(R.id.tl_record)
        return view


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val mainActivityView = (activity as MainActivity)
        val viewPagerAdapter = tabAdapter_record(mainActivityView)

        // fragment add
        viewPagerAdapter.addFragment(fragment_collections())
        viewPagerAdapter.addFragment(fragment_wishes())

        // adapter 연결
        viewPager_record?.adapter = viewPagerAdapter
        viewPager_record?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position + 1}")
            }
        })
        // 뷰페이저와 탭레이아웃을 붙임
        TabLayoutMediator(tabLayout_record, viewPager_record) { tab, position ->
            when (position) {
                0 -> tab.text = "컬렉션"
                1 -> tab.text = "위시"
                else -> tab.text = "Tab ${position + 1}"
            }
        }.attach()


    }
    //-------------------------------------------------------

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_record.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_record().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}