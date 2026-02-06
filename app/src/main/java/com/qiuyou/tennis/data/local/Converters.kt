package com.qiuyou.tennis.data.local

import androidx.room.TypeConverter
import com.qiuyou.tennis.data.model.ActivityStatus
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.PaymentMethod
import com.qiuyou.tennis.data.model.PaymentStatus
import com.qiuyou.tennis.data.model.RegistrationStatus

class Converters {
    @TypeConverter
    fun fromActivityType(value: ActivityType): String = value.name
    
    @TypeConverter
    fun toActivityType(value: String): ActivityType = ActivityType.valueOf(value)
    
    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): String = value.name
    
    @TypeConverter
    fun toActivityStatus(value: String): ActivityStatus = ActivityStatus.valueOf(value)
    
    @TypeConverter
    fun fromRegistrationStatus(value: RegistrationStatus): String = value.name
    
    @TypeConverter
    fun toRegistrationStatus(value: String): RegistrationStatus = RegistrationStatus.valueOf(value)
    
    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name
    
    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? = value?.let { PaymentMethod.valueOf(it) }
    
    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name
    
    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = PaymentStatus.valueOf(value)
}
