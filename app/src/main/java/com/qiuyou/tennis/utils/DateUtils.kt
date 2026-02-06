package com.qiuyou.tennis.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    /**
     * Get day of week in Chinese
     */
    fun getDayOfWeek(timeMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "周一"
            Calendar.TUESDAY -> "周二"
            Calendar.WEDNESDAY -> "周三"
            Calendar.THURSDAY -> "周四"
            Calendar.FRIDAY -> "周五"
            Calendar.SATURDAY -> "周六"
            Calendar.SUNDAY -> "周日"
            else -> ""
        }
    }
    
    /**
     * Get time period (上午/下午/晚上)
     */
    fun getTimePeriod(timeMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "上午"
            in 12..17 -> "下午"
            else -> "晚上"
        }
    }
    
    /**
     * Format time for display (HH:mm)
     */
    fun formatTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }
    
    /**
     * Format date for display (MM月dd日)
     */
    fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("MM月dd日", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }
    
    /**
     * Generate activity title
     * Format: 周X上午/下午 - 地点 - 活动类型
     */
    fun generateActivityTitle(
        startTime: Long,
        locationName: String,
        activityType: String
    ): String {
        val dayOfWeek = getDayOfWeek(startTime)
        val timePeriod = getTimePeriod(startTime)
        return "$dayOfWeek$timePeriod - $locationName - $activityType"
    }
    
    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timeMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timeMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}
