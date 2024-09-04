package com.judahben149.fourthwall.di.module

import com.judahben149.fourthwall.data.local.OrderDao
import com.judahben149.fourthwall.data.local.UserAccountDao
import com.judahben149.fourthwall.data.remote.VerifiableCredentialsService
import com.judahben149.fourthwall.data.repository.CredentialsRepository
import com.judahben149.fourthwall.data.repository.OrderRepository
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import com.judahben149.fourthwall.utils.text.PfiDataParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesOrderRepository(orderDao: OrderDao): OrderRepository {
        return OrderRepository(orderDao)
    }

    @Provides
    @Singleton
    fun providesUserAccountRepository(
        userAccountDao: UserAccountDao,
        pfiDataParser: PfiDataParser
    ): UserAccountRepository {
        return UserAccountRepository(userAccountDao, pfiDataParser)
    }

    @Provides
    @Singleton
    fun providesCredentialsRepository(
        verifiableCredentialsService: VerifiableCredentialsService
    ): CredentialsRepository {
        return CredentialsRepository(verifiableCredentialsService)
    }
}