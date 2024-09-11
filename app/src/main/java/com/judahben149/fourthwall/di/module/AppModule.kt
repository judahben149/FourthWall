package com.judahben149.fourthwall.di.module

import android.content.Context
import android.content.SharedPreferences
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.utils.Constants.ENCRYPTED_SHARED_PREFERENCES
import com.judahben149.fourthwall.utils.Constants.SHARED_PREFERENCES
import com.judahben149.fourthwall.utils.text.PfiDataParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
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
    fun providesSessionManager(
        @Named(SHARED_PREFERENCES)
        sharedPreferences: SharedPreferences,
        @Named(ENCRYPTED_SHARED_PREFERENCES)
        encryptedSharedPreferences: SharedPreferences,
        @ApplicationContext context: Context
    ): SessionManager {
        return SessionManager(sharedPreferences, encryptedSharedPreferences, context)
    }

    @Provides
    @Singleton
    fun providesPfiDataParser(context: Context): PfiDataParser {
        return PfiDataParser(context)
    }
}