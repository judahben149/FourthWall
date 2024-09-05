package com.judahben149.fourthwall.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.domain.usecase.orders.GetAllOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.orders.InsertOrderListUseCase
import com.judahben149.fourthwall.domain.usecase.orders.InsertOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.orders.UpdateOrderStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    getAllOrdersUseCase: GetAllOrdersUseCase,
    private val insertOrdersUseCase: InsertOrdersUseCase,
    private val insertOrderListUseCase: InsertOrderListUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
): ViewModel() {

    private val _state = MutableStateFlow(OrderState(getAllOrdersUseCase()))
    val state: StateFlow<OrderState> = _state

    fun insertOrder(order: OrderEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertOrdersUseCase(order)
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateOrderStatusUseCase(orderId, newStatus)
        }
    }

    fun insertOrderList(orders: List<OrderEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            insertOrderListUseCase(orders)
        }
    }
}

data class OrderState(
    val allOrders: Flow<List<OrderEntity>>
)