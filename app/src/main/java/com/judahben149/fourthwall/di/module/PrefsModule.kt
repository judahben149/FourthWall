package com.judahben149.fourthwall.di.module

import android.content.Context
import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.FOURTH_WALL_SHARED_PREF, Context.MODE_PRIVATE)
    }
}