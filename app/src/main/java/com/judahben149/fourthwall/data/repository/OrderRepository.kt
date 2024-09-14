package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.OrderDao
import com.judahben149.fourthwall.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository (private val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun insert(orderEntity: OrderEntity) {
        orderDao.insertOrder(orderEntity)
    }

    suspend fun insert(orderEntities: List<OrderEntity>) {
        orderDao.insertOrderList(orderEntities)
    }

    suspend fun updateOrderStatus(orderId: Int, newStatus: Int) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    fun getOrderById(orderId: Int): Flow<OrderEntity?> {
        return orderDao.getOrderById(orderId)
    }
}