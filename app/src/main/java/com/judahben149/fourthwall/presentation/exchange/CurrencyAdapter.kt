package com.judahben149.fourthwall.presentation.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemCurrencyBinding
import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.utils.CurrencyUtils

class CurrencyAdapter(
    private val context: Context,
    private val onCurrencySelected: (Currency, Int?) -> Unit
) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var currencies: List<Currency> = emptyList()

    fun updateCurrencies(newCurrencies: List<Currency>) {
        currencies = newCurrencies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount() = currencies.size

    inner class CurrencyViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: Currency) {
            val countryFlag = CurrencyUtils.getCountryFlag(context, currency.code)
            Glide.with(binding.ivFlag).load(countryFlag).into(binding.ivFlag)

            binding.tvCurrencyName.text =
                context.getString(R.string.item_currency_text, currency.name, currency.code)

            itemView.setOnClickListener { onCurrencySelected(currency, countryFlag) }
        }
    }
}