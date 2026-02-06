package com.qiuyou.tennis.data.repository

import com.qiuyou.tennis.data.local.ActivityDao
import com.qiuyou.tennis.data.local.RegistrationDao
import com.qiuyou.tennis.data.local.UserDao
import com.qiuyou.tennis.data.model.RegistrationEntity
import com.qiuyou.tennis.data.model.RegistrationStatus
import com.qiuyou.tennis.data.model.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepository @Inject constructor(
    private val registrationDao: RegistrationDao,
    private val activityDao: ActivityDao,
    private val userDao: UserDao
) {
    
    /**
     * Register for an activity
     */
    suspend fun registerActivity(
        activityId: String,
        userId: String
    ): Result<RegistrationEntity> {
        delay(1000)
        
        return try {
            // Check if already registered
            val existing = registrationDao.getRegistrationByUserAndActivity(userId, activityId)
            if (existing != null) {
                return Result.Error("您已报名此活动")
            }
            
            // Check activity capacity
            val activity = activityDao.getActivityById(activityId)
            if (activity == null) {
                return Result.Error("活动不存在")
            }
            
            val currentCount = registrationDao.getRegistrationsCountByActivity(activityId)
            if (currentCount >= activity.maxParticipants) {
                return Result.Error("活动已满员")
            }
            
            // Create registration
            val registration = RegistrationEntity(
                id = "reg_${System.currentTimeMillis()}",
                activityId = activityId,
                userId = userId,
                status = RegistrationStatus.CONFIRMED,
                registeredAt = System.currentTimeMillis(),
                cancelledAt = null
            )
            
            registrationDao.insertRegistration(registration)
            
            // Update activity current participants
            val updatedActivity = activity.copy(
                currentParticipants = currentCount + 1
            )
            activityDao.updateActivity(updatedActivity)
            
            Result.Success(registration)
        } catch (e: Exception) {
            Result.Error(e.message ?: "报名失败")
        }
    }
    
    /**
     * Cancel registration
     */
    suspend fun cancelRegistration(
        activityId: String,
        userId: String
    ): Result<Unit> {
        delay(500)
        
        return try {
            val registration = registrationDao.getRegistrationByUserAndActivity(userId, activityId)
            if (registration == null) {
                return Result.Error("未找到报名记录")
            }
            
            registrationDao.deleteRegistration(registration)
            
            // Update activity current participants
            val activity = activityDao.getActivityById(activityId)
            if (activity != null) {
                val currentCount = registrationDao.getRegistrationsCountByActivity(activityId)
                val updatedActivity = activity.copy(
                    currentParticipants = currentCount
                )
                activityDao.updateActivity(updatedActivity)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "取消报名失败")
        }
    }
    
    /**
     * Get participants for an activity
     */
    fun getParticipants(activityId: String): Flow<Result<List<UserEntity>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            val registrations = registrationDao.getRegistrationsByActivity(activityId)
            val userIds = registrations.map { it.userId }
            
            val users = userIds.mapNotNull { userId ->
                userDao.getUserById(userId)
            }
            
            emit(Result.Success(users))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取参与者失败"))
        }
    }
    
    /**
     * Check if user is registered for activity
     */
    suspend fun isRegistered(activityId: String, userId: String): Boolean {
        return try {
            registrationDao.getRegistrationByUserAndActivity(userId, activityId) != null
        } catch (e: Exception) {
            false
        }
    }
}
