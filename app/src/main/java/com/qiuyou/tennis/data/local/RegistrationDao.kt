package com.qiuyou.tennis.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiuyou.tennis.data.model.RegistrationEntity
import com.qiuyou.tennis.data.model.RegistrationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Query("SELECT * FROM registrations WHERE userId = :userId AND status != 'CANCELLED' ORDER BY registeredAt DESC")
    fun getUserRegistrations(userId: String): Flow<List<RegistrationEntity>>
    
    @Query("SELECT * FROM registrations WHERE activityId = :activityId AND status != 'CANCELLED'")
    fun getActivityRegistrations(activityId: String): Flow<List<RegistrationEntity>>
    
    @Query("SELECT * FROM registrations WHERE id = :registrationId")
    suspend fun getRegistrationById(registrationId: String): RegistrationEntity?
    
    @Query("SELECT * FROM registrations WHERE activityId = :activityId AND userId = :userId AND status != 'CANCELLED' LIMIT 1")
    suspend fun getUserActivityRegistration(activityId: String, userId: String): RegistrationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: RegistrationEntity)
    
    @Update
    suspend fun updateRegistration(registration: RegistrationEntity)
    
    @Delete
    suspend fun deleteRegistration(registration: RegistrationEntity)
    
    @Query("UPDATE registrations SET status = :status, cancelledAt = :cancelledAt WHERE id = :registrationId")
    suspend fun updateRegistrationStatus(registrationId: String, status: RegistrationStatus, cancelledAt: Long? = null)
    
    @Query("SELECT * FROM registrations WHERE userId = :userId AND status != 'CANCELLED'")
    suspend fun getRegistrationsByUser(userId: String): List<RegistrationEntity>
    
    @Query("SELECT * FROM registrations WHERE activityId = :activityId AND status != 'CANCELLED'")
    suspend fun getRegistrationsByActivity(activityId: String): List<RegistrationEntity>
    
    @Query("SELECT * FROM registrations WHERE activityId = :activityId AND userId = :userId AND status != 'CANCELLED' LIMIT 1")
    suspend fun getRegistrationByUserAndActivity(userId: String, activityId: String): RegistrationEntity?
    
    @Query("SELECT COUNT(*) FROM registrations WHERE activityId = :activityId AND status != 'CANCELLED'")
    suspend fun getRegistrationsCountByActivity(activityId: String): Int
}
