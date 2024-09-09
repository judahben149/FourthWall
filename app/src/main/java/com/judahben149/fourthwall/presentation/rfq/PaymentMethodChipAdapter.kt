package com.judahben149.fourthwall.presentation.rfq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.databinding.ItemChipBinding

class PaymentMethodChipAdapter(
    private val onChipClicked: (String) -> Unit
) :
    RecyclerView.Adapter<PaymentMethodChipAdapter.PaymentMethodChipViewHolder>() {

    private var methods: List<Pair<String, Boolean>> = emptyList()

    fun updatePaymentMethods(newMethods: List<Pair<String, Boolean>>) {
        methods = newMethods
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

        fun bind(paymentMethod: Pair<String, Boolean>) {
            binding.root.text = paymentMethod.first
            binding.root.isChipIconVisible = paymentMethod.second

            binding.root.run {
                text = paymentMethod.first
                isChipIconVisible = paymentMethod.second

                setOnClickListener {
                    onChipClicked(paymentMethod.first)
                }
            }
        }
    }
}
