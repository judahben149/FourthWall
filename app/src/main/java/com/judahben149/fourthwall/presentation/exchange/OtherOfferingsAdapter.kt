package com.judahben149.fourthwall.presentation.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemOtherOfferingsBinding
import com.judahben149.fourthwall.utils.log
import tbdex.sdk.protocol.models.Offering

class OtherOfferingsAdapter(
    private val context: Context,
    private val onOfferingSelected: (String) -> Unit
) :
    RecyclerView.Adapter<OtherOfferingsAdapter.OfferingsViewHolder>() {

    private var offerings: List<Offering> = emptyList()
    private var pfiNames: List<String> = emptyList()
    private var pfiRatings: Map<String, Double> = emptyMap()
    private var highestRatedPfi: String? = null

    fun updateOfferings(
        newOfferings: List<Pair<String, Offering>>,
        pfiRatings: Map<String, Double>
    ) {
        offerings = newOfferings.map { it.second }
        pfiNames = newOfferings.map { it.first }
        this.pfiRatings = pfiRatings

        this.pfiRatings.setPfiWithHighestRating()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferingsViewHolder {
        val binding =
            ItemOtherOfferingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferingsViewHolder, position: Int) {
        val bestOffering = offerings.maxByOrNull { it.data.payoutUnitsPerPayinUnit }
        val isBestOffer = offerings[position].metadata.id == bestOffering?.metadata?.id
        isBestOffer.log("Is Best offer? -->")

        holder.bind(offerings[position], pfiNames[position], isBestOffer)
    }

    override fun getItemCount() = offerings.size

    private fun Map<String, Double>.setPfiWithHighestRating() {
        highestRatedPfi = entries.maxByOrNull { it.value }?.key
    }


    inner class OfferingsViewHolder(private val binding: ItemOtherOfferingsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(offering: Offering, pfiName: String, isBestOffer: Boolean) {
            val payInCurrency = offering.data.payin.currencyCode
            val payOutCurrency = offering.data.payout.currencyCode
            val payOutUnits = offering.data.payoutUnitsPerPayinUnit

            binding.run {
                tvPfiProvider.text = pfiName

                try {
                    val rating = pfiRatings.getValue(offering.metadata.from)
                    val formattedRating = "%.1f".format(rating)

                    listOf(tvPfiRating, ivPfiRating).forEach { it.visibility = View.VISIBLE }
                    tvPfiRating.text = formattedRating
                    tvPfiNoRating.visibility = View.INVISIBLE
                } catch (ex: Exception) {
                    tvPfiNoRating.visibility = View.VISIBLE
                }

                tvValueDetail.text = context.getString(
                    R.string.value_detail_placeholder,
                    payInCurrency,
                    payOutUnits,
                    payOutCurrency
                )

                if (isBestOffer && offerings.size > 1) {
                    tvBestOffer.visibility = View.VISIBLE
                } else {
                    tvBestOffer.visibility = View.INVISIBLE
                }
            }

            itemView.setOnClickListener { onOfferingSelected(offering.metadata.id) }
        }
    }
}