package com.judahben149.fourthwall.presentation.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemOrdersBinding
import com.judahben149.fourthwall.domain.models.Order
import com.judahben149.fourthwall.domain.models.enums.OrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.utils.CurrencyUtils
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import com.judahben149.fourthwall.utils.formatDate

class OrderAdapter(
    private val context: Context
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orders: List<Order> = emptyList()

    fun submitOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(private val binding: ItemOrdersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvReceiverName.text = order.receiverName
                tvAmount.text = order.payInAmount.formatCurrency(order.payInCurrency)
                tvOrderTime.text =
                    context.getString(R.string.order_time, formatDate(order.orderTime))

                tvInTransit.visibility = View.INVISIBLE
                tvSuccess.visibility = View.INVISIBLE
                tvFailure.visibility = View.INVISIBLE

                if (order.orderType == OrderType.SENT) {
                    when (order.orderStatus) {
                        OrderStatus.IN_TRANSIT -> tvInTransit.visibility = View.VISIBLE
                        OrderStatus.SUCCESSFUL -> tvSuccess.visibility = View.VISIBLE
                        OrderStatus.FAILED -> tvFailure.visibility = View.VISIBLE
                    }
                }

                tvOrderType.text = if (order.orderType == OrderType.SENT) "Sent" else "Received"

                val countryFlag = CurrencyUtils.getCountryFlag(context, order.payInCurrency)
                Glide.with(ivCurrencyIcon)
                    .load(countryFlag)
                    .into(ivCurrencyIcon)
            }
        }
    }
}