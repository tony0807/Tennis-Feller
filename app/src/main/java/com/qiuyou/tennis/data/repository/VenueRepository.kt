package com.qiuyou.tennis.data.repository

import com.qiuyou.tennis.data.local.VenueDao
import com.qiuyou.tennis.data.model.VenueEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VenueRepository @Inject constructor(
    private val venueDao: VenueDao
) {
    
    /**
     * Get venues by city
     */
    fun getVenuesByCity(city: String): Flow<Result<List<VenueEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            venueDao.getVenuesByCity(city).collect { venues ->
                if (venues.isEmpty()) {
                    // Initialize with mock data for this city
                    val mockVenues = generateMockVenues(city)
                    venueDao.insertVenues(mockVenues)
                    emit(Result.Success(mockVenues))
                } else {
                    emit(Result.Success(venues))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取球场失败"))
        }
    }
    
    /**
     * Get all cities
     */
    suspend fun getAllCities(): List<String> {
        return try {
            val cities = venueDao.getAllCities()
            if (cities.isEmpty()) {
                // Return default cities
                listOf("北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市")
            } else {
                cities
            }
        } catch (e: Exception) {
            listOf("北京市", "上海市", "广州市", "深圳市")
        }
    }
    
    /**
     * Get current city (mock - based on location)
     */
    suspend fun getCurrentCity(): String {
        delay(500)
        // Mock: return Beijing as default
        return "北京市"
    }
    
    /**
     * Generate mock venues for a city
     */
    private fun generateMockVenues(city: String): List<VenueEntity> {
        val baseId = city.hashCode()
        return listOf(
            VenueEntity(
                id = "venue_${baseId}_1",
                name = "${city}网球中心",
                city = city,
                address = "${city}朝阳区体育路1号",
                latitude = 39.9 + (baseId % 10) * 0.01,
                longitude = 116.4 + (baseId % 10) * 0.01,
                courtCount = 12,
                priceRange = "100-200元/小时",
                facilities = "室内场地,淋浴,停车场,餐厅",
                phone = "010-12345678",
                openHours = "06:00-22:00",
                rating = 4.8f
            ),
            VenueEntity(
                id = "venue_${baseId}_2",
                name = "${city}奥林匹克网球公园",
                city = city,
                address = "${city}奥林匹克公园内",
                latitude = 39.9 + (baseId % 10) * 0.01 + 0.02,
                longitude = 116.4 + (baseId % 10) * 0.01 + 0.02,
                courtCount = 24,
                priceRange = "80-150元/小时",
                facilities = "室外场地,更衣室,停车场",
                phone = "010-23456789",
                openHours = "06:00-21:00",
                rating = 4.6f
            ),
            VenueEntity(
                id = "venue_${baseId}_3",
                name = "${city}体育大学网球场",
                city = city,
                address = "${city}海淀区学院路",
                latitude = 39.9 + (baseId % 10) * 0.01 - 0.02,
                longitude = 116.4 + (baseId % 10) * 0.01 - 0.02,
                courtCount = 8,
                priceRange = "60-120元/小时",
                facilities = "室外场地,更衣室",
                phone = "010-34567890",
                openHours = "07:00-20:00",
                rating = 4.4f
            ),
            VenueEntity(
                id = "venue_${baseId}_4",
                name = "${city}国际网球俱乐部",
                city = city,
                address = "${city}CBD核心区",
                latitude = 39.9 + (baseId % 10) * 0.01 + 0.03,
                longitude = 116.4 + (baseId % 10) * 0.01 - 0.03,
                courtCount = 16,
                priceRange = "150-300元/小时",
                facilities = "室内场地,VIP休息室,淋浴,餐厅,教练服务",
                phone = "010-45678901",
                openHours = "06:00-23:00",
                rating = 4.9f
            ),
            VenueEntity(
                id = "venue_${baseId}_5",
                name = "${city}社区网球中心",
                city = city,
                address = "${city}丰台区社区体育中心",
                latitude = 39.9 + (baseId % 10) * 0.01 - 0.03,
                longitude = 116.4 + (baseId % 10) * 0.01 + 0.03,
                courtCount = 6,
                priceRange = "50-100元/小时",
                facilities = "室外场地,停车场",
                phone = "010-56789012",
                openHours = "06:00-20:00",
                rating = 4.2f
            )
        )
    }
}
