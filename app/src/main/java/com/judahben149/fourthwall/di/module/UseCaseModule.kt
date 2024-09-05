package com.judahben149.fourthwall.di.module

import com.judahben149.fourthwall.data.repository.CredentialsRepository
import com.judahben149.fourthwall.data.repository.OfferingsRepository
import com.judahben149.fourthwall.data.repository.OrderRepository
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import com.judahben149.fourthwall.domain.usecase.orders.GetAllOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.orders.InsertOrderListUseCase
import com.judahben149.fourthwall.domain.usecase.user.GetKccUseCase
import com.judahben149.fourthwall.domain.usecase.orders.InsertOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.pfi.GetPfiOfferingsUseCase
import com.judahben149.fourthwall.domain.usecase.user.InsertUserWithCurrencyAccountsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun providesInsertOrdersUseCase(orderRepository: OrderRepository): InsertOrdersUseCase {
        return InsertOrdersUseCase(orderRepository)
    }

    @Provides
    @Singleton
    fun providesInsertOrderListUseCase(orderRepository: OrderRepository): InsertOrderListUseCase {
        return InsertOrderListUseCase(orderRepository)
    }

    @Provides
    @Singleton
    fun providesGetAllOrdersUseCase(orderRepository: OrderRepository): GetAllOrdersUseCase {
        return GetAllOrdersUseCase(orderRepository)
    }

    @Provides
    @Singleton
    fun providesGetKccUseCase(credentialsRepository: CredentialsRepository): GetKccUseCase {
        return GetKccUseCase(credentialsRepository)
    }

    @Provides
    @Singleton
    fun providesInsertUserWithCurrencyAccountsUseCase(
        userAccountRepository: UserAccountRepository
    ): InsertUserWithCurrencyAccountsUseCase {
        return InsertUserWithCurrencyAccountsUseCase(userAccountRepository)
    }

    @Provides
    @Singleton
    fun providesGetPfiOfferingsUseCase(
        offeringsRepository: OfferingsRepository
    ): GetPfiOfferingsUseCase {
        return GetPfiOfferingsUseCase(offeringsRepository)
    }
}