package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val courtCount: Int = 1,
    val facilities: String? = null, // JSON string
    val contactPhone: String? = null,
    val images: String? = null, // JSON array string
    val createdAt: Long = System.currentTimeMillis()
)
