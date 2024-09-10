package com.judahben149.fourthwall.presentation.home

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemUserAccountBinding
import com.judahben149.fourthwall.domain.models.CurrencyAccount
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class UserAccountAdapter(
    private val context: Context,
    private val rootView: ViewGroup,
    private val windowBackground: Drawable,
    private val onSendMoneyClick: (Int) -> Unit,
    private val onAddAccountClick: (Int) -> Unit,
    private val onFundAccountClick: (Int) -> Unit
) : RecyclerView.Adapter<UserAccountAdapter.UserAccountViewHolder>() {

    private var currencyAccountItems: List<CurrencyAccount> = emptyList()

    fun updateItems(items: List<CurrencyAccount>) {
        currencyAccountItems = items
        notifyDataSetChanged()
    }

    inner class UserAccountViewHolder(private val binding: ItemUserAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = currencyAccountItems[position]
            binding.tvBalanceText.text = if (item.isPrimaryAccount == 1) "Main balance:" else "Secondary balance:"
            binding.tvAmountBalance.text = item.balance.formatCurrency(item.currencyCode)



            // Set BlurView
            binding.blurView
                .setupWith(rootView, getBlurAlgorithm())
                .setFrameClearDrawable(windowBackground)
                .setBlurAutoUpdate(true)

            // Set colours
            getColours(position).let {
                binding.layoutBlur.setCardBackgroundColor(ColorStateList.valueOf(it.cardBg))
                binding.btnSendMoney.backgroundTintList = ColorStateList.valueOf(it.btnSendMoney)
                binding.btnAddAccount.backgroundTintList = ColorStateList.valueOf(it.btnAddAccount)
                binding.btnFundWallet.backgroundTintList = ColorStateList.valueOf(it.btnFund)
            }

            binding.btnSendMoney.setOnClickListener { onSendMoneyClick(position) }
            binding.btnAddAccount.setOnClickListener { onAddAccountClick(position) }
            binding.btnFundWallet.setOnClickListener { onFundAccountClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAccountViewHolder {
        val binding = ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserAccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAccountViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = currencyAccountItems.size


    private fun getBlurAlgorithm(): BlurAlgorithm {

        val algorithm: BlurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffectBlur()
        } else {
            RenderScriptBlur(context)
        }

        return algorithm
    }

    private fun getColours(position: Int): CardColours {
        return when(position) {
            0 -> {
                CardColours(
                    cardBg = context.getColor(R.color.card_1_bg),
                    btnFund = context.getColor(R.color.card_1_chip),
                    btnSendMoney = context.getColor(R.color.card_1_send_btn),
                    btnAddAccount = context.getColor(R.color.card_1_add_btn),
                )
            }

            1 -> {
                CardColours(
                    cardBg = context.getColor(R.color.card_2_bg),
                    btnFund = context.getColor(R.color.card_2_chip),
                    btnSendMoney = context.getColor(R.color.card_2_send_btn),
                    btnAddAccount = context.getColor(R.color.card_2_add_btn),
                )
            }

            2 -> {
                CardColours(
                    cardBg = context.getColor(R.color.card_3_bg),
                    btnFund = context.getColor(R.color.card_3_chip),
                    btnSendMoney = context.getColor(R.color.card_3_send_btn),
                    btnAddAccount = context.getColor(R.color.card_3_add_btn),
                )
            }


            else -> {
                CardColours(
                    cardBg = context.getColor(R.color.card_1_bg),
                    btnFund = context.getColor(R.color.card_1_chip),
                    btnSendMoney = context.getColor(R.color.card_1_send_btn),
                    btnAddAccount = context.getColor(R.color.card_1_add_btn),
                )
            }
        }
    }
}

data class CardColours(
    @ColorInt val cardBg: Int,
    @ColorInt val btnFund: Int,
    @ColorInt val btnSendMoney: Int,
    @ColorInt val btnAddAccount: Int,
)