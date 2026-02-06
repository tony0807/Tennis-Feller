package com.qiuyou.tennis.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.ActivityStatus
import com.qiuyou.tennis.data.model.ActivityType
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    // Get activities by type starting from now (no past activities)
    @Query("SELECT * FROM activities WHERE type = :type AND startTime >= :currentTime AND status = :status ORDER BY startTime ASC")
    fun getActivitiesByType(type: ActivityType, currentTime: Long = System.currentTimeMillis(), status: ActivityStatus = ActivityStatus.OPEN): Flow<List<ActivityEntity>>
    
    @Query("SELECT * FROM activities WHERE type = :type AND startTime >= :startTime AND startTime < :endTime AND status = :status ORDER BY startTime ASC")
    fun getActivitiesByTypeAndDate(
        type: ActivityType,
        startTime: Long,
        endTime: Long,
        status: ActivityStatus = ActivityStatus.OPEN
    ): Flow<List<ActivityEntity>>

    @Query("SELECT COUNT(*) FROM activities WHERE type = :type AND startTime >= :startTime AND startTime < :endTime AND status = :status")
    suspend fun getActivitiesCountByTypeAndDate(
        type: ActivityType,
        startTime: Long,
        endTime: Long,
        status: ActivityStatus = ActivityStatus.OPEN
    ): Int
    
    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityEntity?
    
    @Query("SELECT * FROM activities WHERE id = :activityId")
    fun getActivityByIdFlow(activityId: String): Flow<ActivityEntity?>
    
    @Query("SELECT * FROM activities WHERE creatorId = :userId ORDER BY startTime DESC")
    fun getActivitiesByCreator(userId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE creatorId = :userId AND type = :type ORDER BY startTime DESC")
    fun getActivitiesByCreatorAndType(userId: String, type: ActivityType): Flow<List<ActivityEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)
    
    @Update
    suspend fun updateActivity(activity: ActivityEntity)
    
    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)
    
    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: String)
    
    @Query("UPDATE activities SET currentParticipants = :count WHERE id = :activityId")
    suspend fun updateParticipantCount(activityId: String, count: Int)
    
    @Query("UPDATE activities SET status = :status WHERE id = :activityId")
    suspend fun updateActivityStatus(activityId: String, status: ActivityStatus)
}
