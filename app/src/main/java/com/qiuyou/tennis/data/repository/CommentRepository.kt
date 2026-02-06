package com.qiuyou.tennis.data.repository

import com.qiuyou.tennis.data.local.ActivityDao
import com.qiuyou.tennis.data.local.CommentDao
import com.qiuyou.tennis.data.local.RatingDao
import com.qiuyou.tennis.data.model.CommentEntity
import com.qiuyou.tennis.data.model.RatingEntity
import com.qiuyou.tennis.data.remote.dto.CommentData
import com.qiuyou.tennis.data.remote.dto.UserData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentDao: CommentDao,
    private val ratingDao: RatingDao
) {
    
    fun getComments(activityId: String): Flow<Result<List<CommentEntity>>> = flow {
        emit(Result.Loading)
        delay(500)
        
        try {
            // Get from local database
            commentDao.getCommentsByActivity(activityId).collect { comments ->
                if (comments.isEmpty()) {
                    // Generate mock comments
                    val mockComments = generateMockComments(activityId)
                    commentDao.insertComments(mockComments)
                    emit(Result.Success(mockComments))
                } else {
                    emit(Result.Success(comments))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取评论失败"))
        }
    }
    
    suspend fun postComment(
        activityId: String,
        userId: String,
        userName: String,
        content: String,
        rating: Int?
    ): Result<CommentEntity> {
        delay(1000)
        
        return try {
            val comment = CommentEntity(
                id = "comment_${System.currentTimeMillis()}",
                activityId = activityId,
                userId = userId,
                userName = userName,
                userAvatar = null,
                content = content,
                rating = rating,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            commentDao.insertComment(comment)
            Result.Success(comment)
        } catch (e: Exception) {
            Result.Error(e.message ?: "发表评论失败")
        }
    }
    
    suspend fun deleteComment(commentId: String): Result<Unit> {
        delay(500)
        
        return try {
            commentDao.deleteCommentById(commentId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "删除评论失败")
        }
    }
    
    fun getActivityRating(activityId: String): Flow<Result<Pair<Double, Int>>> = flow {
        emit(Result.Loading)
        delay(300)
        
        try {
            val avgRating = ratingDao.getAverageRating(activityId) ?: 0.0
            val count = ratingDao.getRatingCount(activityId)
            emit(Result.Success(Pair(avgRating, count)))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取评分失败"))
        }
    }
    
    suspend fun rateActivity(
        activityId: String,
        userId: String,
        rating: Int,
        comment: String?
    ): Result<RatingEntity> {
        delay(1000)
        
        return try {
            val ratingEntity = RatingEntity(
                id = "rating_${System.currentTimeMillis()}",
                activityId = activityId,
                userId = userId,
                rating = rating,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            
            ratingDao.insertRating(ratingEntity)
            Result.Success(ratingEntity)
        } catch (e: Exception) {
            Result.Error(e.message ?: "评分失败")
        }
    }
    
    private fun generateMockComments(activityId: String): List<CommentEntity> {
        return listOf(
            CommentEntity(
                id = "comment_1",
                activityId = activityId,
                userId = "user_1",
                userName = "张三",
                userAvatar = null,
                content = "活动组织得很好，大家水平都差不多，打得很开心！",
                rating = 5,
                createdAt = System.currentTimeMillis() - 86400000,
                updatedAt = System.currentTimeMillis() - 86400000
            ),
            CommentEntity(
                id = "comment_2",
                activityId = activityId,
                userId = "user_2",
                userName = "李四",
                userAvatar = null,
                content = "场地不错，教练很专业，推荐！",
                rating = 4,
                createdAt = System.currentTimeMillis() - 172800000,
                updatedAt = System.currentTimeMillis() - 172800000
            )
        )
    }
}
