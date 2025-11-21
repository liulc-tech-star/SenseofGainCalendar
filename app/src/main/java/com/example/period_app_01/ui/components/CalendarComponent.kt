package com.example.period_app_01.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

/**
 * 日历组件
 * 显示年月、日期网格，支持日期选择和表情显示
 */
@Composable
fun CalendarComponent(
    currentYearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    emojiMap: Map<LocalDate, String>,
    tempEmojiMap: Map<LocalDate, String>,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(currentYearMonth) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset > 100) {
                            // 向右滑动，切换到上个月
                            onMonthChange(currentYearMonth.minusMonths(1))
                        } else if (dragOffset < -100) {
                            // 向左滑动，切换到下个月
                            onMonthChange(currentYearMonth.plusMonths(1))
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        // 星期标题行
        WeekDaysHeader()
        
        // 日期网格
        CalendarGrid(
            yearMonth = currentYearMonth,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            emojiMap = emojiMap,
            tempEmojiMap = tempEmojiMap
        )
    }
}

/**
 * 星期标题行
 */
@Composable
private fun WeekDaysHeader() {
    val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 日期网格
 */
@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    emojiMap: Map<LocalDate, String>,
    tempEmojiMap: Map<LocalDate, String>
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=周一, 7=周日
    
    val daysInMonth = yearMonth.lengthOfMonth()
    // 固定显示6行
    val totalWeeks = 6
    
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        // 固定显示6行
        for (week in 0 until totalWeeks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 1..7) {
                    val cellIndex = week * 7 + dayOfWeek
                    val dayOfMonth = cellIndex - firstDayOfWeek + 1
                    
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayOfMonth)
                        val isSelected = date == selectedDate
                        // 优先从 tempEmojiMap 获取，其次从 emojiMap 获取
                        val emoji = tempEmojiMap[date] ?: emojiMap[date]
                        
                        DateCell(
                            date = date,
                            isSelected = isSelected,
                            emoji = emoji,
                            onClick = { onDateSelected(date) }
                        )
                    } else {
                        // 空白单元格
                        Box(modifier = Modifier.weight(1f).height(48.dp))
                    }
                }
            }
        }
    }
}

/**
 * 日期单元格
 */
@Composable
private fun RowScope.DateCell(
    date: LocalDate,
    isSelected: Boolean,
    emoji: String?,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(2.dp)
            .then(
                if (isToday) {
                    // 今日使用红色边框
                    Modifier
                        .border(2.dp, MaterialTheme.colorScheme.error, CircleShape)
                        .clip(CircleShape)
                } else {
                    Modifier.clip(CircleShape)
                }
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 显示表情或日期
            if (!emoji.isNullOrEmpty()) {
                // 显示表情
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            } else {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}
