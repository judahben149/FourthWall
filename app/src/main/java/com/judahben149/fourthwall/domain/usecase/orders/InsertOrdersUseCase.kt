package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.data.repository.OrderRepository
import javax.inject.Inject


class InsertOrdersUseCase @Inject constructor(private val orderRepository: OrderRepository) {

    suspend operator fun invoke(orderEntity: OrderEntity) {
        orderRepository.insert(orderEntity)
    }
}