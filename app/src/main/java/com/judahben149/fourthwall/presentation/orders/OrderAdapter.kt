package com.judahben149.fourthwall.presentation.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemOrdersBinding
import com.judahben149.fourthwall.domain.models.FwOrder
import com.judahben149.fourthwall.domain.models.enums.FwOrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.utils.CurrencyUtils
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import com.judahben149.fourthwall.utils.formatDate

class OrderAdapter(
    private val context: Context,
    private val onItemClicked:(Int) -> Unit
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var fwOrders: List<FwOrder> = emptyList()

    fun submitOrders(newFwOrders: List<FwOrder>) {
        fwOrders = newFwOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(fwOrders[position])
    }

    override fun getItemCount() = fwOrders.size

    inner class OrderViewHolder(private val binding: ItemOrdersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fwOrder: FwOrder) {
            binding.apply {
                tvReceiverName.text = fwOrder.receiverName
                tvAmount.text = fwOrder.payInAmount.formatCurrency(fwOrder.payInCurrency)
                tvOrderTime.text =
                    context.getString(R.string.order_time, formatDate(fwOrder.orderTime))

                itemCard.setOnClickListener { onItemClicked(fwOrder.orderId) }

                tvInTransit.visibility = View.INVISIBLE
                tvSuccess.visibility = View.INVISIBLE
                tvFailure.visibility = View.INVISIBLE

                if (fwOrder.orderType == OrderType.SENT) {
                    when (fwOrder.fwOrderStatus) {
                        FwOrderStatus.IN_TRANSIT -> tvInTransit.visibility = View.VISIBLE
                        FwOrderStatus.SUCCESSFUL -> tvSuccess.visibility = View.VISIBLE
                        FwOrderStatus.FAILED -> tvFailure.visibility = View.VISIBLE
                        FwOrderStatus.PENDING -> tvPending.visibility = View.VISIBLE
                    }
                }

                tvOrderType.text = if (fwOrder.orderType == OrderType.SENT) "Sent" else "Received"

                val countryFlag = CurrencyUtils.getCountryFlag(context, fwOrder.payInCurrency)
                Glide.with(ivCurrencyIcon)
                    .load(countryFlag)
                    .into(ivCurrencyIcon)
            }
        }
    }
}