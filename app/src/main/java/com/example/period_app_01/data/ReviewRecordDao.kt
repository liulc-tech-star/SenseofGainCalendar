package com.example.period_app_01.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import java.time.LocalDate

/**
 * 复盘记录数据访问对象
 */
@Dao
interface ReviewRecordDao {
    
    /**
     * 插入或更新记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ReviewRecord)
    
    /**
     * 根据日期删除记录
     */
    @Query("DELETE FROM review_records WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)
    
    /**
     * 获取指定日期的记录（非Flow）
     */
    @Query("SELECT * FROM review_records WHERE date = :date")
    suspend fun getRecordByDateSync(date: LocalDate): ReviewRecord?
    
    /**
     * 获取某个月份的所有记录（非Flow）
     */
    @Query("SELECT * FROM review_records WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getRecordsByMonthSync(startDate: LocalDate, endDate: LocalDate): List<ReviewRecord>
}
