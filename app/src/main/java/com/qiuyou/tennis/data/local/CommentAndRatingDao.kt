package com.qiuyou.tennis.data.local

import androidx.room.*
import com.qiuyou.tennis.data.model.CommentEntity
import com.qiuyou.tennis.data.model.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE activityId = :activityId ORDER BY createdAt DESC")
    fun getCommentsByActivity(activityId: String): Flow<List<CommentEntity>>
    
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)
    
    @Delete
    suspend fun deleteComment(comment: CommentEntity)
    
    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)
    
    @Query("DELETE FROM comments WHERE activityId = :activityId")
    suspend fun deleteCommentsByActivity(activityId: String)
}

@Dao
interface RatingDao {
    @Query("SELECT * FROM ratings WHERE activityId = :activityId")
    fun getRatingsByActivity(activityId: String): Flow<List<RatingEntity>>
    
    @Query("SELECT AVG(rating) FROM ratings WHERE activityId = :activityId")
    suspend fun getAverageRating(activityId: String): Double?
    
    @Query("SELECT COUNT(*) FROM ratings WHERE activityId = :activityId")
    suspend fun getRatingCount(activityId: String): Int
    
    @Query("SELECT * FROM ratings WHERE activityId = :activityId AND userId = :userId")
    suspend fun getUserRating(activityId: String, userId: String): RatingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: RatingEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatings(ratings: List<RatingEntity>)
    
    @Delete
    suspend fun deleteRating(rating: RatingEntity)
}
