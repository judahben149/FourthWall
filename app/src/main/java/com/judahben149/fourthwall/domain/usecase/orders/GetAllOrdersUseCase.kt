package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.data.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllOrdersUseCase @Inject constructor(private val orderRepository: OrderRepository) {

    operator fun invoke(): Flow<List<OrderEntity>> {
        return orderRepository.allOrders
    }
}