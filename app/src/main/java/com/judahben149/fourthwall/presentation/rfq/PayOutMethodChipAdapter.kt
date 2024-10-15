package com.judahben149.fourthwall.presentation.rfq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemChipBinding
import com.judahben149.fourthwall.utils.text.formatAddSpace
import com.judahben149.fourthwall.utils.views.CustomIconChip

class PayOutMethodChipAdapter(
    private val onChipClicked: (String) -> Unit
) :
    RecyclerView.Adapter<PayOutMethodChipAdapter.PaymentMethodChipViewHolder>() {

    private var methods: List<String> = emptyList()
    private var isPayOutFieldFilled: Boolean = false

    fun updatePaymentKinds(updatedKinds: List<String>, isPayOutFieldFilled: Boolean) {
        methods = updatedKinds
        this.isPayOutFieldFilled = isPayOutFieldFilled
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
                text = paymentMethod.formatAddSpace()
                toggleTick(isPayOutFieldFilled)

                setOnClickListener {
                    onChipClicked(paymentMethod)
                }
            }
        }
    }

    private fun CustomIconChip.toggleTick(shouldBeTicked: Boolean) {
        if (shouldBeTicked) {
//            setPadding(64, 12, 16, 12)
            setIconResource(R.drawable.ic_tick)
            isChipIconVisible = true
        } else {
//            setPadding(16, 12, 16, 12)
        }
    }
}
