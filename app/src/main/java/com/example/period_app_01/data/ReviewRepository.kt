package com.example.period_app_01.data

import java.time.LocalDate

/**
 * 复盘记录数据仓库
 * 封装数据库操作，解耦ViewModel与数据库
 */
class ReviewRepository(private val reviewRecordDao: ReviewRecordDao) {
    
    /**
     * 获取指定日期的记录（同步）
     */
    suspend fun getRecordByDateSync(date: LocalDate): ReviewRecord? {
        return reviewRecordDao.getRecordByDateSync(date)
    }
    
    /**
     * 获取某个月份的所有记录（同步）
     */
    suspend fun getRecordsByMonthSync(startDate: LocalDate, endDate: LocalDate): List<ReviewRecord> {
        return reviewRecordDao.getRecordsByMonthSync(startDate, endDate)
    }
    
    /**
     * 保存或更新记录
     */
    suspend fun saveRecord(record: ReviewRecord) {
        reviewRecordDao.insertRecord(record.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * 根据日期删除记录
     */
    suspend fun deleteByDate(date: LocalDate) {
        reviewRecordDao.deleteByDate(date)
    }
}
