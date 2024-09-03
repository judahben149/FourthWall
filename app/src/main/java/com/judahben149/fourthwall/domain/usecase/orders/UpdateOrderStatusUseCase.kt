package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.repository.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(private val orderRepository: OrderRepository) {

    suspend operator fun invoke(orderId: Int, newStatus: Int) {
        orderRepository.updateOrderStatus(orderId, newStatus)
    }
}