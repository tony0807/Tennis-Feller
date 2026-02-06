package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val activityId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val rating: Int?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey
    val id: String,
    val activityId: String,
    val userId: String,
    val rating: Int, // 1-5 stars
    val comment: String?,
    val createdAt: Long
)
