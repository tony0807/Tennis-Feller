package com.qiuyou.tennis.data.remote.dto

// ==================== Request DTOs ====================

data class PhoneLoginRequest(
    val phone: String,
    val verificationCode: String
)

data class WeChatLoginRequest(
    val code: String,
    val userInfo: WeChatUserInfo
)

data class WeChatUserInfo(
    val nickname: String,
    val avatar: String,
    val openId: String
)

data class RegisterRequest(
    val phone: String,
    val verificationCode: String,
    val nickname: String?,
    val skillLevel: String?
)

data class UpdateProfileRequest(
    val nickname: String?,
    val avatar: String?,
    val skillLevel: String?,
    val signature: String?
)

data class CreateActivityRequest(
    val title: String,
    val type: String,
    val date: String,
    val time: String,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val skillLevel: String,
    val maxParticipants: Int,
    val pricePerPerson: Double,
    val description: String,
    val images: List<String>
)

data class PostCommentRequest(
    val content: String,
    val rating: Int?
)

data class RatingRequest(
    val rating: Int,
    val comment: String?
)

data class PaymentRequest(
    val activityId: String,
    val amount: Double,
    val paymentMethod: String
)

// ==================== Response DTOs ====================

data class BaseResponse(
    val code: Int,
    val message: String,
    val success: Boolean
)

data class LoginResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: LoginData?
)

data class LoginData(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val phone: String,
    val nickname: String,
    val avatar: String?,
    val skillLevel: String?,
    val signature: String?
)

data class ActivityListResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: ActivityListData?
)

data class ActivityListData(
    val activities: List<ActivityData>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

data class ActivityData(
    val id: String,
    val title: String,
    val type: String,
    val date: String,
    val time: String,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val skillLevel: String,
    val currentParticipants: Int,
    val maxParticipants: Int,
    val pricePerPerson: Double,
    val description: String,
    val images: List<String>,
    val creator: UserData,
    val status: String,
    val createdAt: String
)

data class RegistrationResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: RegistrationData?
)

data class RegistrationData(
    val registrationId: String,
    val activityId: String,
    val userId: String,
    val status: String,
    val paymentRequired: Boolean,
    val paymentAmount: Double?
)

data class CommentListResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: CommentListData?
)

data class CommentListData(
    val comments: List<CommentData>,
    val total: Int,
    val page: Int
)

data class CommentData(
    val id: String,
    val activityId: String,
    val user: UserData,
    val content: String,
    val rating: Int?,
    val createdAt: String
)

data class CommentResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: CommentData?
)

data class RatingResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: RatingData?
)

data class RatingData(
    val id: String,
    val activityId: String,
    val userId: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String
)

data class RatingStatsResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: RatingStatsData?
)

data class RatingStatsData(
    val averageRating: Double,
    val totalRatings: Int,
    val distribution: Map<Int, Int>
)

data class ImageUploadResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: ImageData?
)

data class ImageData(
    val url: String,
    val thumbnail: String?,
    val width: Int,
    val height: Int
)

data class PaymentResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: PaymentData?
)

data class PaymentData(
    val paymentId: String,
    val prepayId: String?,
    val qrCode: String?,
    val deepLink: String?
)

data class PaymentStatusResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: PaymentStatusData?
)

data class PaymentStatusData(
    val paymentId: String,
    val status: String,
    val paidAt: String?,
    val amount: Double
)
