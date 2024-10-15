package com.judahben149.fourthwall.presentation.rfq

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemChipBinding
import com.judahben149.fourthwall.utils.text.formatAddSpace
import com.judahben149.fourthwall.utils.views.CustomIconChip

class PayInMethodChipAdapter(
    private val onChipClicked: (String) -> Unit
) :
    RecyclerView.Adapter<PayInMethodChipAdapter.PaymentMethodChipViewHolder>() {

    private var methods: List<String> = emptyList()
    private var isPayInFieldFilled: Boolean = false

    fun updatePaymentKinds(updatedKinds: List<String>, isPayInFieldFilled: Boolean) {
        methods = updatedKinds
        this.isPayInFieldFilled = isPayInFieldFilled
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
                toggleTick(isPayInFieldFilled)

                setOnClickListener {
                    onChipClicked(paymentMethod)
                }
            }
        }
    }

    private fun CustomIconChip.toggleTick(shouldBeTicked: Boolean) {
        if (shouldBeTicked) {

            val strokeWidthInDp = 1.2F
            val strokeWidthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, strokeWidthInDp.toFloat(), resources.displayMetrics
            )

            chipStrokeWidth = strokeWidthInPx
            setIconResource(R.drawable.ic_tick)

            isChipIconVisible = true
        }
    }
}
