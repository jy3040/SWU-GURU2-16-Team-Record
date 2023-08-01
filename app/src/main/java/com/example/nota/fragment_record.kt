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
class fragment_record : Fragment() {
    //탭바 화면 이동 구현
    private lateinit var viewPager_record: ViewPager2
    private lateinit var tabLayout_record: TabLayout

    private lateinit var fb_record_floating: FloatingActionButton
    private lateinit var fb_record_floating_c: FloatingActionButton
    private lateinit var tv_record_floating_c: TextView
    private lateinit var fb_record_floating_w: FloatingActionButton
    private lateinit var tv_record_floating_w: TextView

    // 플로팅 액션 버튼 및 텍스트뷰를 보여줄지 여부를 나타내는 변수
    var isRunning:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 화면을 구성하는 뷰를 인플레이트합니다.
        val view= inflater.inflate(R.layout.fragment_record, container, false)

        // 플로팅 액션 버튼과 관련된 뷰들을 초기화합니다.
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
}