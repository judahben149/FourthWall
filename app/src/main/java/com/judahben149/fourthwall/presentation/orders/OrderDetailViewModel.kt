package com.judahben149.fourthwall.presentation.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.models.FwOrder
import com.judahben149.fourthwall.domain.usecase.orders.GetOrderDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: Int = checkNotNull(savedStateHandle["orderId"])

    private val _orderDetail = MutableStateFlow<FwOrder?>(null)
    val orderDetail: StateFlow<FwOrder?> = _orderDetail.asStateFlow()

    init {
        viewModelScope.launch {
            getOrderDetailUseCase(orderId).collect { order ->
                _orderDetail.value = order
            }
        }
    }
}