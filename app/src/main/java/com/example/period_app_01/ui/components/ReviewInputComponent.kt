package com.example.period_app_01.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.period_app_01.data.QuickPhrases

/**
 * 复盘输入组件
 * 提供三行输入框和快捷填充功能
 */
@Composable
fun ReviewInputComponent(
    threeThings: String,
    achievement: String,
    feeling: String,
    onThreeThingsChange: (String) -> Unit,
    onAchievementChange: (String) -> Unit,
    onFeelingChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickPhrases by remember { mutableStateOf(false) }
    var currentField by remember { mutableStateOf(0) } // 0: 三件事, 1: 成果, 2: 心情
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 输入框1：微成果
        ReviewInputField(
            label = "微成果",
            value = threeThings,
            onValueChange = onThreeThingsChange,
            placeholder = "记录今天完成的主要事项...",
            onShowQuickPhrases = {
                currentField = 0
                showQuickPhrases = true
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 输入梆2：简评价
        ReviewInputField(
            label = "简评价",
            value = achievement,
            onValueChange = onAchievementChange,
            placeholder = "分享今天的收获或成就...",
            onShowQuickPhrases = {
                currentField = 1
                showQuickPhrases = true
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 输入梆3：轻感受
        ReviewInputField(
            label = "轻感受",
            value = feeling,
            onValueChange = onFeelingChange,
            placeholder = "记录今天的心情和感受...",
            onShowQuickPhrases = {
                currentField = 2
                showQuickPhrases = true
            }
        )
        
        // 快捷短语选择对话框
        if (showQuickPhrases) {
            QuickPhrasesDialog(
                fieldType = currentField,
                onDismiss = { showQuickPhrases = false },
                onPhraseSelected = { phrase ->
                    when (currentField) {
                        0 -> {
                            val current = threeThings
                            onThreeThingsChange(if (current.isEmpty()) phrase else "$current\n$phrase")
                        }
                        1 -> {
                            val current = achievement
                            onAchievementChange(if (current.isEmpty()) phrase else "$current\n$phrase")
                        }
                        2 -> {
                            val current = feeling
                            onFeelingChange(if (current.isEmpty()) phrase else "$current\n$phrase")
                        }
                    }
                    showQuickPhrases = false
                }
            )
        }
    }
}

/**
 * 单个输入框
 */
@Composable
private fun ReviewInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onShowQuickPhrases: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            TextButton(
                onClick = onShowQuickPhrases,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text("快捷填充", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            minLines = 2,
            maxLines = 4,
            shape = MaterialTheme.shapes.medium
        )
    }
}

/**
 * 快捷短语选择对话框
 */
@Composable
private fun QuickPhrasesDialog(
    fieldType: Int,
    onDismiss: () -> Unit,
    onPhraseSelected: (String) -> Unit
) {
    val phrases = when (fieldType) {
        0 -> QuickPhrases.threeThingsOptions
        1 -> QuickPhrases.achievementOptions
        2 -> QuickPhrases.feelingOptions
        else -> emptyList()
    }
    
    val title = when (fieldType) {
        0 -> "选择事项"
        1 -> "选择成果"
        2 -> "选择心情"
        else -> "选择内容"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                phrases.forEach { phrase ->
                    TextButton(
                        onClick = { onPhraseSelected(phrase) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = phrase,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
