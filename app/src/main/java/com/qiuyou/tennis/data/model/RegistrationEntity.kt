package com.qiuyou.tennis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class RegistrationEntity(
    @PrimaryKey
    val id: String,
    val activityId: String,
    val userId: String,
    val status: RegistrationStatus = RegistrationStatus.PENDING,
    val paymentMethod: PaymentMethod? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val registeredAt: Long = System.currentTimeMillis(),
    val cancelledAt: Long? = null
)

enum class RegistrationStatus {
    PENDING,    // 待确认
    CONFIRMED,  // 已确认
    CANCELLED   // 已取消
}

enum class PaymentMethod {
    WECHAT,     // 微信支付
    ALIPAY,     // 支付宝
    BANK        // 银行卡
}

enum class PaymentStatus {
    UNPAID,     // 未支付
    PAID,       // 已支付
    REFUNDED    // 已退款
}
