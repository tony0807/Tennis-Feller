package com.qiuyou.tennis.data.repository

import com.qiuyou.tennis.data.local.ActivityDao
import com.qiuyou.tennis.data.local.RegistrationDao
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.RegistrationEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val activityDao: ActivityDao,
    private val registrationDao: RegistrationDao
) {
    
    /**
     * Get activities by type and optional date
     */
    fun getActivities(typeString: String, date: String? = null): Flow<Result<List<ActivityEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            val type = when (typeString.uppercase()) {
                "PLAY" -> ActivityType.PLAY
                "EVENT", "TOURNAMENT" -> ActivityType.TOURNAMENT
                "COURSE" -> ActivityType.COURSE
                else -> ActivityType.PLAY
            }
            
            if (date != null) {
                // Parse date (expected format: yyyy-MM-dd)
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
                val startDate = sdf.parse(date)
                val calendar = java.util.Calendar.getInstance()
                calendar.time = startDate
                val startTime = calendar.timeInMillis
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                val endTime = calendar.timeInMillis
                
                activityDao.getActivitiesByTypeAndDate(type, startTime, endTime).collect { activities ->
                    emit(Result.Success(activities))
                }
            } else {
                // Pass current time to filter out past activities
                activityDao.getActivitiesByType(type, System.currentTimeMillis()).collect { activities ->
                    emit(Result.Success(activities))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取活动失败"))
        }
    }
    
    /**
     * Get activity by ID
     */
    suspend fun getActivityById(activityId: String): Result<ActivityEntity> {
        delay(200)
        
        return try {
            val activity = activityDao.getActivityById(activityId)
            if (activity != null) {
                Result.Success(activity)
            } else {
                Result.Error("活动不存在")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "获取活动详情失败")
        }
    }
    
    /**
     * Create new activity
     */
    suspend fun createActivity(activity: ActivityEntity): Result<ActivityEntity> {
        delay(1000)
        
        return try {
            activityDao.insertActivity(activity)
            Result.Success(activity)
        } catch (e: Exception) {
            Result.Error(e.message ?: "创建活动失败")
        }
    }
    
    /**
     * Update activity
     */
    suspend fun updateActivity(activity: ActivityEntity): Result<ActivityEntity> {
        delay(500)
        
        return try {
            activityDao.updateActivity(activity)
            Result.Success(activity)
        } catch (e: Exception) {
            Result.Error(e.message ?: "更新活动失败")
        }
    }
    
    /**
     * Delete activity
     */
    suspend fun deleteActivity(activityId: String): Result<Unit> {
        delay(500)
        
        return try {
            activityDao.deleteActivityById(activityId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "删除活动失败")
        }
    }
    
    /**
     * Get activities count by date
     */
    suspend fun getActivitiesCountByDate(typeString: String, dates: List<Long>): Map<Long, Int> {
        val type = when (typeString.uppercase()) {
            "PLAY" -> ActivityType.PLAY
            "EVENT", "TOURNAMENT" -> ActivityType.TOURNAMENT
            "COURSE" -> ActivityType.COURSE
            else -> ActivityType.PLAY
        }
        
        val counts = mutableMapOf<Long, Int>()
        val calendar = java.util.Calendar.getInstance()
        
        dates.forEach { dateInMillis ->
            calendar.timeInMillis = dateInMillis
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis
            
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            val endTime = calendar.timeInMillis
            
            val count = activityDao.getActivitiesCountByTypeAndDate(type, startTime, endTime)
            counts[dateInMillis] = count
        }
        
        return counts
    }
    
    /**
     * Get user's created activities by type
     */
    fun getMyActivitiesByType(userId: String, typeString: String): Flow<Result<List<ActivityEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            val type = when (typeString.uppercase()) {
                "PLAY" -> ActivityType.PLAY
                "EVENT", "TOURNAMENT" -> ActivityType.TOURNAMENT
                "COURSE" -> ActivityType.COURSE
                else -> ActivityType.PLAY
            }
            
            activityDao.getActivitiesByCreatorAndType(userId, type).collect { activities ->
                emit(Result.Success(activities))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取我的活动失败"))
        }
    }
    
    /**
     * Get user's created activities
     */
    fun getMyActivities(userId: String): Flow<Result<List<ActivityEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            activityDao.getActivitiesByCreator(userId).collect { activities ->
                emit(Result.Success(activities))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取我的活动失败"))
        }
    }
    
    /**
     * Get registered activities for user
     */
    fun getRegisteredActivities(userId: String): Flow<Result<List<ActivityEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            // Get registration IDs
            val registrations = registrationDao.getRegistrationsByUser(userId)
            val activityIds = registrations.map { it.activityId }
            
            // Get activities
            val activities = activityIds.mapNotNull { activityId ->
                activityDao.getActivityById(activityId)
            }
            
            emit(Result.Success(activities))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取已报名活动失败"))
        }
    }
}
