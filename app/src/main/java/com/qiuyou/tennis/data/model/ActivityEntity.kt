package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val type: ActivityType,
    val title: String,
    val creatorId: String,
    val clubId: String? = null,
    val customLocation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // minutes
    val maxParticipants: Int,
    val currentParticipants: Int = 0,
    val fee: Double = 0.0,
    val skillLevel: String, // "2.5以下", "2.5", "3.0", "3.0+", etc.
    val activityType: String, // "单打", "双打"
    val description: String? = null,
    val images: String? = null, // JSON array string
    val creatorParticipates: Boolean = true, // 发布者是否参加
    val status: ActivityStatus = ActivityStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ActivityType {
    PLAY,       // 约球
    COURSE,     // 课程
    TOURNAMENT  // 赛事
}

enum class ActivityStatus {
    OPEN,       // 开放报名
    FULL,       // 已满员
    CANCELLED,  // 已取消
    COMPLETED   // 已完成
}
