package com.example.nota

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val fragmentHome by lazy { fragment_home() }
    private val fragmentRecord by lazy { fragment_record() }
    private val fragmentReport by lazy { fragment_report() }
    private val fragmentSetting by lazy { fragment_setting() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bnv_activity_main)

        // 바텀네비게이션 아이콘 클릭 이벤트
        bottomNavigationView.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.it_bottom_navigation_home -> {
                        replaceFragment(fragmentHome)
                    }
                    R.id.it_bottom_navigation_record -> {
                        replaceFragment(fragmentRecord)
                    }
                    R.id.it_bottom_navigation_report -> {
                        replaceFragment(fragmentReport)
                    }
                    R.id.it_bottom_navigation_setting -> {
                        replaceFragment(fragmentSetting)
                    }
                }
                true
            }
            selectedItemId = R.id.it_bottom_navigation_home
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_activity_main, fragment).commit()
    }
}
