package com.qiuyou.tennis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.ClubEntity
import com.qiuyou.tennis.data.model.CommentEntity
import com.qiuyou.tennis.data.model.RatingEntity
import com.qiuyou.tennis.data.model.RegistrationEntity
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.model.VenueEntity

@Database(
    entities = [
        UserEntity::class,
        ClubEntity::class,
        ActivityEntity::class,
        RegistrationEntity::class,
        CommentEntity::class,
        RatingEntity::class,
        VenueEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clubDao(): ClubDao
    abstract fun activityDao(): ActivityDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun commentDao(): CommentDao
    abstract fun ratingDao(): RatingDao
    abstract fun venueDao(): VenueDao
    
    companion object {
        const val DATABASE_NAME = "tennis_app_database"
    }
}
