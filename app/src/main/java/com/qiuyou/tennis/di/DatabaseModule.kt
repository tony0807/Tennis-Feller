package com.qiuyou.tennis.di

import android.content.Context
import androidx.room.Room
import com.qiuyou.tennis.data.local.ActivityDao
import com.qiuyou.tennis.data.local.AppDatabase
import com.qiuyou.tennis.data.local.ClubDao
import com.qiuyou.tennis.data.local.CommentDao
import com.qiuyou.tennis.data.local.RatingDao
import com.qiuyou.tennis.data.local.RegistrationDao
import com.qiuyou.tennis.data.local.UserDao
import com.qiuyou.tennis.data.local.VenueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For demo purposes
            .build()
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideClubDao(database: AppDatabase): ClubDao {
        return database.clubDao()
    }
    
    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao {
        return database.activityDao()
    }
    
    @Provides
    fun provideRegistrationDao(database: AppDatabase): RegistrationDao {
        return database.registrationDao()
    }
    
    @Provides
    fun provideCommentDao(database: AppDatabase): CommentDao {
        return database.commentDao()
    }
    
    @Provides
    fun provideRatingDao(database: AppDatabase): RatingDao {
        return database.ratingDao()
    }
    
    @Provides
    fun provideVenueDao(database: AppDatabase): VenueDao {
        return database.venueDao()
    }
}
