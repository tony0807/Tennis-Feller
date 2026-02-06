package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "venues")
data class VenueEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val courtCount: Int,
    val priceRange: String, // e.g., "80-150元/小时"
    val facilities: String, // JSON string of facilities
    val phone: String? = null,
    val openHours: String? = null,
    val rating: Float = 4.5f,
    val createdAt: Long = System.currentTimeMillis()
)
