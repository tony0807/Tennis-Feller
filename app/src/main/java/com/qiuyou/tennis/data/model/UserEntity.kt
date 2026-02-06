package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val nickname: String,
    val phone: String,
    val email: String? = null,
    val avatar: String? = null,
    val gender: String, // MALE, FEMALE, OTHER
    val skillLevel: String? = null,
    val signature: String? = null,
    val wechatId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Gender {
    MALE, FEMALE, OTHER
}
