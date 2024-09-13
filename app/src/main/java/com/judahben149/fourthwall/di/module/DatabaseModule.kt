package com.judahben149.fourthwall.di.module

import android.content.Context
import androidx.room.Room
import com.judahben149.fourthwall.data.local.FourthWallDatabase
import com.judahben149.fourthwall.data.local.OrderDao
import com.judahben149.fourthwall.data.local.PfiRatingDao
import com.judahben149.fourthwall.data.local.UserAccountDao
import com.judahben149.fourthwall.utils.Constants.DATABASE_NAME
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
    fun providesFourthWallDatabase(@ApplicationContext context: Context): FourthWallDatabase {

        return Room.databaseBuilder(
            context,
            FourthWallDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun providesOrderDao(fourthWallDatabase: FourthWallDatabase): OrderDao {
        return fourthWallDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun providesUserAccountDao(fourthWallDatabase: FourthWallDatabase): UserAccountDao {
        return fourthWallDatabase.userAccountDao()
    }

    @Provides
    @Singleton
    fun providePfiRatingDao(fourthWallDatabase: FourthWallDatabase): PfiRatingDao {
        return fourthWallDatabase.pfiRatingDao()
    }
}