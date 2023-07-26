package com.example.nota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nota.ui.theme.NotaTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    //{} 안 fragment 변경 필요 (현재 일부만 변경됨)
    private val fragment_home by lazy { fragment_home() }
    private val fragment_record by lazy { fragment_record() }
    private val fragment_report by lazy { fragment_report() }
    private val fragment_setting by lazy { fragment_home() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        // BottomNavigationView
        var bnv_activity_main = findViewById(R.id.bnv_activity_main) as BottomNavigationView

        // 바텀네비게이션 아이콘 클릭 이벤트
        bnv_activity_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.it_bottom_navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_activity_main, fragment_home).commit()
                }
                R.id.it_bottom_navigation_record -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_activity_main, fragment_record).commit()
                }
                R.id.it_bottom_navigation_report -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_activity_main, fragment_report).commit()
                }

                R.id.it_bottom_navigation_setting -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_activity_main, fragment_home).commit()
                }
            }
            true
        }
            selectedItemId = R.id.it_bottom_navigation_home
        }

    }
}

