package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.data.repository.OrderRepository
import javax.inject.Inject

class InsertOrderListUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(orderList: List<OrderEntity>) {
        orderRepository.insert(orderList)
    }
}