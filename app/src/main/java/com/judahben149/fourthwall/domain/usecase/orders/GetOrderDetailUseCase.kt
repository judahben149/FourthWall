package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.repository.OrderRepository
import com.judahben149.fourthwall.domain.mappers.toFwOrder
import com.judahben149.fourthwall.domain.models.FwOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOrderDetailUseCase @Inject constructor(private val orderRepository: OrderRepository) {
    operator fun invoke(orderId: Int): Flow<FwOrder?> = orderRepository.getOrderById(orderId)
        .map { orderEntity ->
            orderEntity?.toFwOrder()
        }
}