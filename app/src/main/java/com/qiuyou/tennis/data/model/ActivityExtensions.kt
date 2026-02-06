package com.qiuyou.tennis.data.model

import com.qiuyou.tennis.presentation.components.ActivityCardData
import java.text.SimpleDateFormat
import java.util.*

fun ActivityEntity.toCardData(): ActivityCardData {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
    val timeStr = sdf.format(Date(this.startTime))
    
    return ActivityCardData(
        id = this.id,
        title = this.title,
        time = timeStr,
        location = this.customLocation ?: "未知地点",
        distance = "未知", // Placeholder
        skillLevel = this.skillLevel,
        activityType = this.activityType,
        currentParticipants = this.currentParticipants,
        maxParticipants = this.maxParticipants,
        pricePerPerson = this.fee
    )
}
