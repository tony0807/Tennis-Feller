package com.qiuyou.tennis.domain.wechat

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock WeChat Manager for login, share, and payment
 * Replace with real WeChat SDK in production
 */
@Singleton
class WeChatManager @Inject constructor() {
    
    /**
     * Mock WeChat login
     */
    suspend fun login(): WeChatLoginResult {
        delay(1500) // Simulate WeChat authorization
        
        return WeChatLoginResult(
            success = true,
            code = "mock_wechat_code_${System.currentTimeMillis()}",
            userInfo = WeChatUserInfo(
                openId = "mock_openid_${System.currentTimeMillis()}",
                nickname = "微信用户",
                avatar = "https://via.placeholder.com/150"
            )
        )
    }
    
    /**
     * Mock WeChat share
     */
    suspend fun shareToFriend(title: String, description: String, imageUrl: String?, webUrl: String?): Boolean {
        delay(1000)
        return true
    }
    
    /**
     * Mock WeChat share to moments
     */
    suspend fun shareToMoments(title: String, description: String, imageUrl: String?, webUrl: String?): Boolean {
        delay(1000)
        return true
    }
    
    /**
     * Mock WeChat pay
     */
    suspend fun pay(prepayId: String, amount: Double): WeChatPayResult {
        delay(2000) // Simulate payment process
        
        return WeChatPayResult(
            success = true,
            transactionId = "mock_transaction_${System.currentTimeMillis()}",
            message = "支付成功"
        )
    }
    
    fun isWeChatInstalled(): Boolean {
        // Mock: always return true for demo
        return true
    }
}

data class WeChatLoginResult(
    val success: Boolean,
    val code: String?,
    val userInfo: WeChatUserInfo?,
    val errorMessage: String? = null
)

data class WeChatUserInfo(
    val openId: String,
    val nickname: String,
    val avatar: String
)

data class WeChatPayResult(
    val success: Boolean,
    val transactionId: String?,
    val message: String
)
