package com.example.period_app_01.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.period_app_01.ui.components.*
import com.example.period_app_01.viewmodel.ReviewViewModel
import java.time.format.DateTimeFormatter

/**
 * 复盘日历主界面
 * 整合日历、复盘输入和表情选择功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewCalendarScreen(
    viewModel: ReviewViewModel,
    modifier: Modifier = Modifier
) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentRecord by viewModel.currentRecord.collectAsState()
    val tempEmojiMap by viewModel.tempEmojiMap.collectAsState()
    val emojiMap by viewModel.emojiMap.collectAsState()
    
    val threeThings by viewModel.threeThings.collectAsState()
    val achievement by viewModel.achievement.collectAsState()
    val feeling by viewModel.feeling.collectAsState()
    val selectedEmoji by viewModel.selectedEmoji.collectAsState()
    val hasContentChanged by viewModel.hasContentChanged.collectAsState()
    
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val saveMessage by viewModel.saveMessage.collectAsState()
    
    val focusManager = LocalFocusManager.current
    
    // 显示保存提示
    LaunchedEffect(saveMessage) {
        if (saveMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSaveMessage()
        }
    }
    
    // 切换日期时清除焦点
    LaunchedEffect(selectedDate) {
        focusManager.clearFocus()
    }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "获得日历  ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${currentYearMonth.year}年${currentYearMonth.monthValue}月",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        },
        bottomBar = {
            // 底部按钮栏
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 删除按钮
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        enabled = currentRecord != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // 回到今天按钮
                    OutlinedButton(
                        onClick = { 
                            viewModel.changeYearMonth(java.time.YearMonth.now())
                            viewModel.selectDate(java.time.LocalDate.now())
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "今天",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("今天")
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // 编辑按钮
                    Button(
                        onClick = { viewModel.showEditDialog() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("编辑")
                    }
                }
            }
        },
        snackbarHost = {
            // 显示保存消息
            if (saveMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(saveMessage!!)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 日历视图
            CalendarComponent(
                currentYearMonth = currentYearMonth,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    viewModel.selectDate(date)
                },
                onMonthChange = { yearMonth ->
                    viewModel.changeYearMonth(yearMonth)
                },
                emojiMap = emojiMap,
                tempEmojiMap = tempEmojiMap,
                modifier = Modifier.fillMaxWidth()
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            // 复盘内容显示区域（只读）
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 无内容提示
                if (threeThings.isEmpty() && achievement.isEmpty() && feeling.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "点击下方编辑按钮开始记录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // 微成果
                    if (threeThings.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Text(
                                    text = "微成果",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Text(
                                text = threeThings,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (achievement.isNotEmpty() || feeling.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    // 简评价
                    if (achievement.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Text(
                                    text = "简评价",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Text(
                                text = achievement,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (feeling.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    // 轻感受
                    if (feeling.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Text(
                                    text = "轻感受",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Text(
                                text = feeling,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除当前记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteCurrentRecord()
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    // 编辑对话框
    if (showEditDialog) {
        EditReviewDialog(
            selectedEmoji = selectedEmoji,
            threeThings = threeThings,
            achievement = achievement,
            feeling = feeling,
            errorMessage = saveMessage,
            onEmojiSelected = { viewModel.updateEmoji(it) },
            onThreeThingsChange = { viewModel.updateThreeThings(it) },
            onAchievementChange = { viewModel.updateAchievement(it) },
            onFeelingChange = { viewModel.updateFeeling(it) },
            onDismiss = { 
                viewModel.clearSaveMessage()
                viewModel.hideEditDialog()
            },
            onSave = {
                focusManager.clearFocus()
                viewModel.clearSaveMessage()
                viewModel.saveRecord()
                // 保存成功后会自动关闭对话框，失败则保持打开
            },
            hasChanges = hasContentChanged
        )
    }
}
