package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentRequestQuoteBinding
import com.judahben149.fourthwall.presentation.exchange.CurrencyAdapter
import com.judahben149.fourthwall.presentation.exchange.OfferingsViewModel
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.text.parseRequiredPaymentDetails
import com.judahben149.fourthwall.utils.views.ChipManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestQuoteFragment : Fragment() {

    private var _binding: FragmentRequestQuoteBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val offeringsViewModel: OfferingsViewModel by hiltNavGraphViewModels(R.id.order_flow_nav)
    private val viewModel: QuoteViewModel by viewModels()
    private lateinit var chipAdapter: PaymentMethodChipAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setData()
        observeState()
        collectPayInPayoutDetails()

        //
//        createRfq()
        seeSelectedOffering()
    }

    private fun setData() {
        offeringsViewModel.state.value.selectedOffering?.let { off ->
            viewModel.updateSelectedOffering(off)
        }

        confirmCredentialsAvailability()

        // Set possible payment methods
        chipAdapter = PaymentMethodChipAdapter()
        val possiblePaymentKinds = viewModel.state.value.possiblePaymentKinds
        val chipPaymentKindList = mutableListOf<Pair<String, Boolean>>()

        possiblePaymentKinds.forEachIndexed { index, possiblePaymentKind ->
            chipPaymentKindList.add(
                Pair(index, possiblePaymentKind.formattedKind)
            )
        }


    }

    private fun observeState() {

    }

    private fun confirmCredentialsAvailability() {
        val areCredentialsAvailable = viewModel.confirmCredentials()

        binding.run {
            if (areCredentialsAvailable) {
                tvCredentialsNotPresent.visibility = View.GONE
                btnRequestCredentials.visibility = View.GONE
                tvCredentialsPresent.visibility = View.VISIBLE
            } else {
                tvCredentialsPresent.visibility = View.GONE
                tvCredentialsNotPresent.visibility = View.VISIBLE
                btnRequestCredentials.visibility = View.VISIBLE
            }
        }
    }

    private fun seeSelectedOffering() {
//        offeringsViewModel.state.value.selectedOffering?.let { offering ->
//            offering.data.requiredClaims.toString().log("Required Claims --->")
//        }
    }

    private fun setListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }
        }
    }

    private fun createRfq() {
        offeringsViewModel.state.value.offeringsList.let {
            it.forEach { off ->
                off.data.payout.methods.forEach { meth ->
                    ("Pay out Method (${off.data.payin.currencyCode} -> ${off.data.payout.currencyCode})- " + meth.kind).log()
                }
            }

            it.forEach { off ->
                off.data.payin.methods.forEach { meth ->
                    ("Pay in Method (${off.data.payin.currencyCode} -> ${off.data.payout.currencyCode})- " + meth.kind).log()
                }
            }
        }
    }

    private fun collectPayInPayoutDetails() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}