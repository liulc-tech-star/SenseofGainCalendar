package com.example.period_app_01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.period_app_01.ui.theme.Period_app_01Theme
import com.example.period_app_01.data.DatesDatabase
import com.example.period_app_01.data.ReviewRepository
import com.example.period_app_01.viewmodel.ReviewViewModel
import com.example.period_app_01.ui.screens.ReviewCalendarScreen

/**
 * 主活动类，应用入口
 * 负责初始化数据库和显示复盘日历界面
 */
class MainActivity : ComponentActivity() {
    
    /**
     * onCreate 初始化活动
     * 初始化数据库和 UI
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化数据库
        val database = DatesDatabase.getDatabase(applicationContext)
        val reviewRecordDao = database.reviewRecordDao()
        val repository = ReviewRepository(reviewRecordDao)
        val viewModel = ReviewViewModel(repository)

        setContent {
            Period_app_01Theme {
                // 显示复盘日历主界面
                ReviewCalendarScreen(viewModel = viewModel)
            }
        }
    }
}
