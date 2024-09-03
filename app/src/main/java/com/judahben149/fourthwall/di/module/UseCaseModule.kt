package com.judahben149.fourthwall.di.module

import com.judahben149.fourthwall.data.repository.CredentialsRepository
import com.judahben149.fourthwall.data.repository.OrderRepository
import com.judahben149.fourthwall.domain.usecase.GetAllOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.GetKccUseCase
import com.judahben149.fourthwall.domain.usecase.InsertOrdersUseCase
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
    fun providesGetAllOrdersUseCase(orderRepository: OrderRepository): GetAllOrdersUseCase {
        return GetAllOrdersUseCase(orderRepository)
    }

    @Provides
    @Singleton
    fun providesGetKccUseCase(credentialsRepository: CredentialsRepository): GetKccUseCase {
        return GetKccUseCase(credentialsRepository)
    }
}