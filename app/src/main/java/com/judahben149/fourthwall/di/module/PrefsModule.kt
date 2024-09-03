package com.judahben149.fourthwall.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.Constants.ENCRYPTED_SHARED_PREFERENCES
import com.judahben149.fourthwall.utils.Constants.FILE_ENCRYPTED_SHARED_PREFERENCES
import com.judahben149.fourthwall.utils.Constants.SHARED_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {

    @Provides
    @Singleton
    @Named(SHARED_PREFERENCES)
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.FOURTH_WALL_SHARED_PREF, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(ENCRYPTED_SHARED_PREFERENCES)
    fun providesEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            return EncryptedSharedPreferences.create(
                FILE_ENCRYPTED_SHARED_PREFERENCES,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }
}