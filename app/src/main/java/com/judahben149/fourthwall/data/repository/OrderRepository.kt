package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.OrderDao
import com.judahben149.fourthwall.data.local.entities.Order
import kotlinx.coroutines.flow.Flow

class OrderRepository (private val orderDao: OrderDao) {

    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun insert(order: Order) {
        orderDao.insertOrder(order)
    }
}