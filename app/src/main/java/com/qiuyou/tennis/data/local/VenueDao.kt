package com.qiuyou.tennis.data.local

import androidx.room.*
import com.qiuyou.tennis.data.model.VenueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VenueDao {
    @Query("SELECT * FROM venues WHERE city = :city ORDER BY rating DESC")
    fun getVenuesByCity(city: String): Flow<List<VenueEntity>>
    
    @Query("SELECT * FROM venues ORDER BY city, rating DESC")
    fun getAllVenues(): Flow<List<VenueEntity>>
    
    @Query("SELECT * FROM venues WHERE id = :venueId")
    suspend fun getVenueById(venueId: String): VenueEntity?
    
    @Query("SELECT DISTINCT city FROM venues ORDER BY city")
    suspend fun getAllCities(): List<String>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenue(venue: VenueEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenues(venues: List<VenueEntity>)
    
    @Delete
    suspend fun deleteVenue(venue: VenueEntity)
    
    @Query("DELETE FROM venues")
    suspend fun deleteAllVenues()
}
