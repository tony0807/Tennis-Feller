package com.qiuyou.tennis.data.repository

import com.qiuyou.tennis.data.local.UserDao
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.remote.TennisApi
import com.qiuyou.tennis.data.remote.dto.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class UserRepository @Inject constructor(
    private val api: TennisApi,
    private val userDao: UserDao
) {
    // Mock current user ID
    private var currentUserId: String? = null
    private var authToken: String? = null
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedInFlow = _isLoggedIn.asStateFlow()
    
    fun loginWithPassword(account: String, password: String): Flow<Result<LoginData>> = flow {
        emit(Result.Loading)
        delay(1000) // Simulate network delay
        
        try {
            // Mock login response
            val mockUser = UserData(
                id = "user_${System.currentTimeMillis()}",
                phone = if (account.all { it.isDigit() }) account else "",
                nickname = "用户${account.takeLast(4)}",
                avatar = null,
                skillLevel = "3.0",
                signature = "热爱网球"
            )
            
            val loginData = LoginData(
                token = "mock_token_${System.currentTimeMillis()}",
                user = mockUser
            )
            
            // Save to local database
            val userEntity = UserEntity(
                id = mockUser.id,
                phone = mockUser.phone,
                username = account,
                nickname = mockUser.nickname,
                email = "",
                avatar = mockUser.avatar,
                skillLevel = "3.0",
                gender = "未知",
                signature = mockUser.signature,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            userDao.insertUser(userEntity)
            
            currentUserId = mockUser.id
            authToken = loginData.token
            _isLoggedIn.value = true
            
            emit(Result.Success(loginData))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "登录失败"))
        }
    }
    
    fun loginWithWeChat(code: String, userInfo: WeChatUserInfo): Flow<Result<LoginData>> = flow {
        emit(Result.Loading)
        delay(1000)
        
        try {
            val mockUser = UserData(
                id = "user_wechat_${userInfo.openId}",
                phone = "",
                nickname = userInfo.nickname,
                avatar = userInfo.avatar,
                skillLevel = "3.0",
                signature = "微信用户"
            )
            
            val loginData = LoginData(
                token = "mock_token_wechat_${System.currentTimeMillis()}",
                user = mockUser
            )
            
            // Save to local database
            val userEntity = UserEntity(
                id = mockUser.id,
                phone = mockUser.phone,
                username = mockUser.nickname,
                nickname = mockUser.nickname,
                email = "",
                avatar = mockUser.avatar,
                skillLevel = "3.0",
                gender = "未知",
                signature = mockUser.signature,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            userDao.insertUser(userEntity)
            
            currentUserId = mockUser.id
            authToken = loginData.token
            _isLoggedIn.value = true
            
            emit(Result.Success(loginData))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "微信登录失败"))
        }
    }
    
    fun register(account: String, password: String, nickname: String?, skillLevel: String?, signature: String?): Flow<Result<LoginData>> = flow {
        emit(Result.Loading)
        delay(1000)
        
        try {
            val mockUser = UserData(
                id = "user_${System.currentTimeMillis()}",
                phone = if (account.all { it.isDigit() }) account else "",
                nickname = nickname ?: "用户${account.takeLast(4)}",
                avatar = null,
                skillLevel = skillLevel ?: "2.5",
                signature = signature
            )
            
            val loginData = LoginData(
                token = "mock_token_${System.currentTimeMillis()}",
                user = mockUser
            )
            
            // Save to local database
            val userEntity = UserEntity(
                id = mockUser.id,
                phone = mockUser.phone,
                username = account,
                nickname = mockUser.nickname,
                email = "",
                avatar = mockUser.avatar,
                skillLevel = mockUser.skillLevel ?: "2.5",
                gender = "未知",
                signature = mockUser.signature,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            userDao.insertUser(userEntity)
            
            currentUserId = mockUser.id
            authToken = loginData.token
            _isLoggedIn.value = true
            
            emit(Result.Success(loginData))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "注册失败"))
        }
    }
    
    fun getUserProfile(): Flow<Result<UserEntity>> = flow {
        emit(Result.Loading)
        delay(500)
        
        try {
            currentUserId?.let { userId ->
                val user = userDao.getUserById(userId)
                if (user != null) {
                    emit(Result.Success(user))
                } else {
                    emit(Result.Error("用户不存在"))
                }
            } ?: emit(Result.Error("未登录"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "获取用户信息失败"))
        }
    }
    
    suspend fun updateProfile(nickname: String?, avatar: String?, skillLevel: String?, signature: String?): Result<UserEntity> {
        delay(1000)
        
        return try {
            currentUserId?.let { userId ->
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updatedUser = user.copy(
                        username = nickname ?: user.username,
                        nickname = nickname ?: user.nickname,
                        avatar = avatar ?: user.avatar,
                        skillLevel = skillLevel ?: user.skillLevel,
                        signature = signature ?: user.signature,
                        updatedAt = System.currentTimeMillis()
                    )
                    userDao.updateUser(updatedUser)
                    Result.Success(updatedUser)
                } else {
                    Result.Error("用户不存在")
                }
            } ?: Result.Error("未登录")
        } catch (e: Exception) {
            Result.Error(e.message ?: "更新失败")
        }
    }
    
    fun getCurrentUserId(): String? = currentUserId
    fun getAuthToken(): String? = authToken
    fun isLoggedIn(): Boolean = currentUserId != null
    
    fun logout() {
        currentUserId = null
        authToken = null
        _isLoggedIn.value = false
    }
}
