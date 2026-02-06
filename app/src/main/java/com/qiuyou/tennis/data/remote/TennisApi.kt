package com.qiuyou.tennis.data.remote

import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Tennis API interface for backend communication
 * Note: This is a mock implementation. Replace base URL with actual backend in production.
 */
interface TennisApi {
    
    // ==================== User APIs ====================
    
    @POST("auth/login/phone")
    suspend fun loginWithPhone(
        @Body request: PhoneLoginRequest
    ): Response<LoginResponse>
    
    @POST("auth/login/wechat")
    suspend fun loginWithWeChat(
        @Body request: WeChatLoginRequest
    ): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>
    
    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserEntity>
    
    @PUT("user/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UserEntity>
    
    // ==================== Activity APIs ====================
    
    @GET("activities")
    suspend fun getActivities(
        @Query("type") type: String? = null,
        @Query("date") date: String? = null,
        @Query("skillLevel") skillLevel: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ActivityListResponse>
    
    @GET("activities/{id}")
    suspend fun getActivityDetail(
        @Path("id") activityId: String
    ): Response<ActivityEntity>
    
    @POST("activities")
    suspend fun createActivity(
        @Header("Authorization") token: String,
        @Body request: CreateActivityRequest
    ): Response<ActivityEntity>
    
    @POST("activities/{id}/register")
    suspend fun registerActivity(
        @Header("Authorization") token: String,
        @Path("id") activityId: String
    ): Response<RegistrationResponse>
    
    @DELETE("activities/{id}/register")
    suspend fun cancelRegistration(
        @Header("Authorization") token: String,
        @Path("id") activityId: String
    ): Response<BaseResponse>
    
    @GET("activities/my")
    suspend fun getMyActivities(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<ActivityListResponse>
    
    // ==================== Comment APIs ====================
    
    @GET("activities/{id}/comments")
    suspend fun getComments(
        @Path("id") activityId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<CommentListResponse>
    
    @POST("activities/{id}/comments")
    suspend fun postComment(
        @Header("Authorization") token: String,
        @Path("id") activityId: String,
        @Body request: PostCommentRequest
    ): Response<CommentResponse>
    
    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("id") commentId: String
    ): Response<BaseResponse>
    
    // ==================== Rating APIs ====================
    
    @POST("activities/{id}/rating")
    suspend fun rateActivity(
        @Header("Authorization") token: String,
        @Path("id") activityId: String,
        @Body request: RatingRequest
    ): Response<RatingResponse>
    
    @GET("activities/{id}/rating")
    suspend fun getActivityRating(
        @Path("id") activityId: String
    ): Response<RatingStatsResponse>
    
    // ==================== Image APIs ====================
    
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part("file") file: okhttp3.MultipartBody.Part
    ): Response<ImageUploadResponse>
    
    // ==================== Payment APIs ====================
    
    @POST("payment/wechat/create")
    suspend fun createWeChatPayment(
        @Header("Authorization") token: String,
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
    
    @GET("payment/{id}/status")
    suspend fun getPaymentStatus(
        @Header("Authorization") token: String,
        @Path("id") paymentId: String
    ): Response<PaymentStatusResponse>
}
