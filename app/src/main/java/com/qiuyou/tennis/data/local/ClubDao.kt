package com.qiuyou.tennis.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiuyou.tennis.data.model.ClubEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs ORDER BY name ASC")
    fun getAllClubs(): Flow<List<ClubEntity>>
    
    @Query("SELECT * FROM clubs WHERE id = :clubId")
    suspend fun getClubById(clubId: String): ClubEntity?
    
    @Query("SELECT * FROM clubs WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchClubs(query: String): Flow<List<ClubEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: ClubEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<ClubEntity>)
    
    @Update
    suspend fun updateClub(club: ClubEntity)
    
    @Delete
    suspend fun deleteClub(club: ClubEntity)
}
