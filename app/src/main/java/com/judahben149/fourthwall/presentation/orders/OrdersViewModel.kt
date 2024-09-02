package com.judahben149.fourthwall.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.data.local.entities.Order
import com.judahben149.fourthwall.domain.usecase.GetAllOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.InsertOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.UpdateOrderStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    getAllOrdersUseCase: GetAllOrdersUseCase,
    private val insertOrdersUseCase: InsertOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
): ViewModel() {

    val allOrders: Flow<List<Order>> = getAllOrdersUseCase()

    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            insertOrdersUseCase(order)
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateOrderStatusUseCase(orderId, newStatus)
        }
    }
}