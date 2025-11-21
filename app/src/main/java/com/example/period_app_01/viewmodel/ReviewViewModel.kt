package com.example.period_app_01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.period_app_01.data.ReviewRecord
import com.example.period_app_01.data.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * 复盘日历ViewModel
 * 管理UI状态和业务逻辑
 */
class ReviewViewModel(
    private val repository: ReviewRepository
) : ViewModel() {
    
    // 当前选中的年月
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()
    
    // 当前选中的日期
    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()
    
    // 当前选中日期的复盘记录
    private val _currentRecord = MutableStateFlow<ReviewRecord?>(null)
    val currentRecord: StateFlow<ReviewRecord?> = _currentRecord.asStateFlow()
    
    // 当前月份的所有记录
    private val _monthRecords = MutableStateFlow<List<ReviewRecord>>(emptyList())
    val monthRecords: StateFlow<List<ReviewRecord>> = _monthRecords.asStateFlow()
    
    // 日期到表情的映射缓存，提升查询性能
    private val _emojiMap = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val emojiMap: StateFlow<Map<LocalDate, String>> = _emojiMap.asStateFlow()
    
    // 临时表情映射，用于立即更新UI
    private val _tempEmojiMap = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val tempEmojiMap: StateFlow<Map<LocalDate, String>> = _tempEmojiMap.asStateFlow()
    
    // 三行输入内容
    private val _threeThings = MutableStateFlow("")
    val threeThings: StateFlow<String> = _threeThings.asStateFlow()
    
    private val _achievement = MutableStateFlow("")
    val achievement: StateFlow<String> = _achievement.asStateFlow()
    
    private val _feeling = MutableStateFlow("")
    val feeling: StateFlow<String> = _feeling.asStateFlow()
    
    // 选中的表情
    private val _selectedEmoji = MutableStateFlow("")
    val selectedEmoji: StateFlow<String> = _selectedEmoji.asStateFlow()
    
    // 是否显示编辑对话框
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()
    
    // 保存状态提示
    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage.asStateFlow()
    
    // 内容是否有变更
    private val _hasContentChanged = MutableStateFlow(false)
    val hasContentChanged: StateFlow<Boolean> = _hasContentChanged.asStateFlow()
    
    // 原始记录内容（用于对比是否有变更）
    private var originalThreeThings = ""
    private var originalAchievement = ""
    private var originalFeeling = ""
    private var originalEmoji = ""
    
    init {
        // 初始化时加载当前日期的记录
        loadRecordForSelectedDate()
        loadMonthRecords()
    }
    
    /**
     * 切换年月
     */
    fun changeYearMonth(yearMonth: YearMonth) {
        _currentYearMonth.value = yearMonth
        // 切换月份时清空临时映射，避免显示其他月份的临时数据
        _tempEmojiMap.value = emptyMap()
        // 立即加载新月份的记录，不要提前清空数据
        loadMonthRecords()
    }
    
    /**
     * 选择日期
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadRecordForSelectedDate()
    }
    
    /**
     * 加载选中日期的记录
     */
    private fun loadRecordForSelectedDate() {
        val date = _selectedDate.value ?: return
        viewModelScope.launch {
            val record = repository.getRecordByDateSync(date)
            _currentRecord.value = record
            
            // 更新输入框内容
            _threeThings.value = record?.threeThings ?: ""
            _achievement.value = record?.achievement ?: ""
            _feeling.value = record?.feeling ?: ""
            _selectedEmoji.value = record?.emoji ?: ""
            
            // 保存原始内容
            originalThreeThings = _threeThings.value
            originalAchievement = _achievement.value
            originalFeeling = _feeling.value
            originalEmoji = _selectedEmoji.value
            
            // 重置变更状态
            _hasContentChanged.value = false
        }
    }
    
    /**
     * 加载当前月份的所有记录
     */
    private fun loadMonthRecords() {
        viewModelScope.launch {
            val yearMonth = _currentYearMonth.value
            val startDate = yearMonth.atDay(1)
            val endDate = yearMonth.atEndOfMonth()
            val records = repository.getRecordsByMonthSync(startDate, endDate)
            // 先构建新的表情映射
            val newEmojiMap = records.associate { it.date to it.emoji }
            // 原子性地同时更新两个状态，避免竞态条件
            _emojiMap.value = newEmojiMap
            _monthRecords.value = records
        }
    }
    
    /**
     * 更新三行输入内容
     */
    fun updateThreeThings(text: String) {
        _threeThings.value = text
        checkContentChanged()
    }
    
    fun updateAchievement(text: String) {
        _achievement.value = text
        checkContentChanged()
    }
    
    fun updateFeeling(text: String) {
        _feeling.value = text
        checkContentChanged()
    }
    
    fun updateEmoji(emoji: String) {
        _selectedEmoji.value = emoji
        checkContentChanged()
    }
    
    /**
     * 检查内容是否有变更
     */
    private fun checkContentChanged() {
        _hasContentChanged.value = 
            _threeThings.value != originalThreeThings ||
            _achievement.value != originalAchievement ||
            _feeling.value != originalFeeling ||
            _selectedEmoji.value != originalEmoji
    }
    
    /**
     * 显示/隐藏编辑对话框
     */
    fun showEditDialog() {
        _showEditDialog.value = true
    }
    
    fun hideEditDialog() {
        _showEditDialog.value = false
    }
    
    /**
     * 保存复盘记录
     * 返回是否保存成功
     */
    fun saveRecord(): Boolean {
        val date = _selectedDate.value
        if (date == null) {
            _saveMessage.value = "请选择日期"
            return false
        }
        
        val threeThingsText = _threeThings.value.trim()
        val achievementText = _achievement.value.trim()
        val feelingText = _feeling.value.trim()
        val emojiText = _selectedEmoji.value.trim()
        
        // 必须选择表情
        if (emojiText.isEmpty()) {
            _saveMessage.value = "请选择心情表情"
            return false
        }
        
        // 必须填写所有三个标签
        if (threeThingsText.isEmpty()) {
            _saveMessage.value = "请填写微成果"
            return false
        }
        if (achievementText.isEmpty()) {
            _saveMessage.value = "请填写简评价"
            return false
        }
        if (feelingText.isEmpty()) {
            _saveMessage.value = "请填写轻感受"
            return false
        }
        
        viewModelScope.launch {
            val record = ReviewRecord(
                date = date,
                threeThings = threeThingsText,
                achievement = achievementText,
                feeling = feelingText,
                emoji = _selectedEmoji.value,
                createdAt = _currentRecord.value?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // 在保存前立即更新临时映射，实现立即显示
            val updatedMap = _tempEmojiMap.value.toMutableMap()
            if (_selectedEmoji.value.isNotEmpty()) {
                updatedMap[date] = _selectedEmoji.value
            } else {
                updatedMap[date] = ""
            }
            _tempEmojiMap.value = updatedMap
            
            // 保存到数据库
            repository.saveRecord(record)
            _currentRecord.value = record
            _saveMessage.value = null
            
            // 更新原始内容
            originalThreeThings = threeThingsText
            originalAchievement = achievementText
            originalFeeling = feelingText
            originalEmoji = _selectedEmoji.value
            
            // 重置变更状态
            _hasContentChanged.value = false
            
            // 重新加载月份记录
            loadMonthRecords()
            
            // 数据库加载完成后，清除该日期的临时映射
            val finalMap = _tempEmojiMap.value.toMutableMap()
            finalMap.remove(date)
            _tempEmojiMap.value = finalMap
            
            // 关闭编辑对话框
            _showEditDialog.value = false
        }
        
        return true
    }
    
    /**
     * 清除保存提示
     */
    fun clearSaveMessage() {
        _saveMessage.value = null
    }
    
    /**
     * 删除当前记录
     */
    fun deleteCurrentRecord() {
        val date = _selectedDate.value ?: return
        viewModelScope.launch {
            // 立即更新临时映射为空字符串，实现立即显示数字
            val updatedMap = _tempEmojiMap.value.toMutableMap()
            updatedMap[date] = ""
            _tempEmojiMap.value = updatedMap
            
            // 从数据库删除
            repository.deleteByDate(date)
            _currentRecord.value = null
            _threeThings.value = ""
            _achievement.value = ""
            _feeling.value = ""
            _selectedEmoji.value = ""
            _saveMessage.value = "删除成功"
            
            // 更新原始内容状态
            originalThreeThings = ""
            originalAchievement = ""
            originalFeeling = ""
            originalEmoji = ""
            _hasContentChanged.value = false
            
            // 重新加载月份记录
            loadMonthRecords()
            
            // 数据库加载完成后，清除该日期的临时映射
            val finalMap = _tempEmojiMap.value.toMutableMap()
            finalMap.remove(date)
            _tempEmojiMap.value = finalMap
        }
    }
    
    /**
     * 获取日期的表情
     */
    fun getEmojiForDate(date: LocalDate): String? {
        return _emojiMap.value[date]
    }
}
