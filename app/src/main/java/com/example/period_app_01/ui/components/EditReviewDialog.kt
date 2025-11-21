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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.period_app_01.data.EmojiList

/**
 * 编辑复盘对话框
 */
@Composable
fun EditReviewDialog(
    selectedEmoji: String,
    threeThings: String,
    achievement: String,
    feeling: String,
    errorMessage: String?,
    onEmojiSelected: (String) -> Unit,
    onThreeThingsChange: (String) -> Unit,
    onAchievementChange: (String) -> Unit,
    onFeelingChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    hasChanges: Boolean
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = "编辑复盘",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 错误提示（显示在顶部）
                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                // 表情选择区域
                Text(
                    text = "选择心情",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 表情网格 - 精简版，只显示表情
                EmojiGridCompact(
                    selectedEmoji = selectedEmoji,
                    onEmojiSelected = onEmojiSelected
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // 使用 ReviewInputComponent
                ReviewInputComponent(
                    threeThings = threeThings,
                    achievement = achievement,
                    feeling = feeling,
                    onThreeThingsChange = onThreeThingsChange,
                    onAchievementChange = onAchievementChange,
                    onFeelingChange = onFeelingChange,
                    modifier = Modifier.padding(0.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 按钮行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        enabled = hasChanges
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

/**
 * 精简版表情网格
 */
@Composable
private fun EmojiGridCompact(
    selectedEmoji: String,
    onEmojiSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 分批显示表情，每行8个
        val emojisPerRow = 8
        val rows = EmojiList.emojis.chunked(emojisPerRow)
        
        rows.forEach { rowEmojis ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowEmojis.forEach { emojiData ->
                    EmojiItemCompact(
                        emoji = emojiData.emoji,
                        isSelected = selectedEmoji == emojiData.emoji,
                        onClick = { onEmojiSelected(emojiData.emoji) }
                    )
                }
                // 填充空白以保持对齐
                repeat(emojisPerRow - rowEmojis.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * 精简版表情项
 */
@Composable
private fun RowScope.EmojiItemCompact(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = if (isSelected) {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
            contentPadding = PaddingValues(4.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
