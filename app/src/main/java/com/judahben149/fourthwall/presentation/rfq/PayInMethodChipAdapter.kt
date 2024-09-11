package com.judahben149.fourthwall.presentation.rfq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.databinding.ItemChipBinding
import com.judahben149.fourthwall.utils.text.formatKind

class PayInMethodChipAdapter(
    private val onChipClicked: (String) -> Unit
) :
    RecyclerView.Adapter<PayInMethodChipAdapter.PaymentMethodChipViewHolder>() {

    private var methods: List<String> = emptyList()

    fun updatePaymentKinds(updatedKinds: List<String>) {
        methods = updatedKinds
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodChipViewHolder {
        val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentMethodChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodChipViewHolder, position: Int) {
        holder.bind(methods[position])
    }

    override fun getItemCount() = methods.size

    inner class PaymentMethodChipViewHolder(
        private val binding: ItemChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(paymentMethod: String) {
            binding.root.run {
                text = paymentMethod.formatKind()

                setOnClickListener {
                    onChipClicked(paymentMethod)
                }
            }
        }
    }
}
