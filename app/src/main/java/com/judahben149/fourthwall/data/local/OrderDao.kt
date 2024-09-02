package com.judahben149.fourthwall.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.judahben149.fourthwall.data.local.entities.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Insert
    suspend fun insertOrder(order: Order)

    @Query("UPDATE orders SET orderStatus = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Int, newStatus: Int)
}