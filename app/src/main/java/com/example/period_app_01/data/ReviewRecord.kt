package com.example.period_app_01.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 复盘记录实体类
 * 用于存储每日复盘数据
 */
@Entity(tableName = "review_records")
data class ReviewRecord(
    @PrimaryKey
    val date: LocalDate,
    
    /**
     * 今日完成3件事
     */
    val threeThings: String = "",
    
    /**
     * 1个小成果/反馈
     */
    val achievement: String = "",
    
    /**
     * 今日心情/感受
     */
    val feeling: String = "",
    
    /**
     * 选择的表情
     * 使用表情Unicode或者标识符
     */
    val emoji: String = "",
    
    /**
     * 记录创建时间
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * 记录更新时间
     */
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 表情数据类
 * 包含表情符号、名称和引导语
 */
data class EmojiData(
    val emoji: String,
    val name: String,
    val guideText: String
)

/**
 * 预定义的正向表情列表
 */
object EmojiList {
    val emojis = listOf(
        EmojiData("😊", "开心", "今天很开心呢！"),
        EmojiData("💪", "加油", "继续保持这份动力！"),
        EmojiData("🎉", "庆祝", "值得庆祝的一天！"),
        EmojiData("⭐", "高效", "今天效率满满！"),
        EmojiData("🌈", "美好", "美好的一天！"),
        EmojiData("✨", "闪光", "闪闪发光的一天！"),
        EmojiData("🔥", "热情", "充满热情的一天！"),
        EmojiData("💖", "充实", "内心充实满足！"),
        EmojiData("🌟", "优秀", "表现真棒！"),
        EmojiData("😌", "平和", "心情平和舒适！"),
        EmojiData("🎯", "专注", "专注力爆棚！"),
        EmojiData("🌺", "愉悦", "心情愉悦轻松！"),
        EmojiData("🚀", "进步", "有明显进步！"),
        EmojiData("🌱", "成长", "今天有所成长！"),
        EmojiData("💡", "灵感", "有了新的想法！"),
        EmojiData("🏆", "胜利", "取得了胜利！")
    )
}

/**
 * 快捷填充短语
 */
object QuickPhrases {
    // 今日完成3件事的快捷短语
    val threeThingsOptions = listOf(
        "完成工作任务",
        "学习新知识",
        "锻炼身体",
        "整理房间",
        "陪伴家人",
        "阅读书籍",
        "写作记录",
        "解决问题"
    )
    
    // 小成果/反馈的快捷短语
    val achievementOptions = listOf(
        "收到正面反馈",
        "解决了困扰已久的问题",
        "完成了重要目标",
        "学会了新技能",
        "帮助了他人",
        "突破了自我",
        "有了新想法",
        "得到了认可"
    )
    
    // 心情/感受的快捷短语
    val feelingOptions = listOf(
        "心情愉悦",
        "充满动力",
        "平静满足",
        "积极向上",
        "充实快乐",
        "放松自在",
        "信心满满",
        "感恩美好"
    )
}
