package com.judahben149.fourthwall.di.module

import android.content.Context
import android.content.SharedPreferences
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.utils.PfiDataParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesSessionManager(sharedPreferences: SharedPreferences): SessionManager {
        return SessionManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providesPfiDataParser(context: Context): PfiDataParser {
        return PfiDataParser(context)
    }
}