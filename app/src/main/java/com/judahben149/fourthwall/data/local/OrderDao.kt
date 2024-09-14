package com.judahben149.fourthwall.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judahben149.fourthwall.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert
    suspend fun insertOrder(orderEntity: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderList(orderEntities: List<OrderEntity>)

    @Query("UPDATE orders SET orderStatus = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Int, newStatus: Int)

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: Int): Flow<OrderEntity?>
}