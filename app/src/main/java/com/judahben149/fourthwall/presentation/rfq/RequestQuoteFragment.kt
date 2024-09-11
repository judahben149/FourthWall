package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentRequestQuoteBinding
import com.judahben149.fourthwall.presentation.exchange.OfferingsViewModel
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showInfoAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        //
//        createRfq()
        seeSelectedOffering()
    }

    private fun setData() {
        offeringsViewModel.state.value.let { state ->
            state.selectedOffering?.let { viewModel.updateSelectedOffering(it) }
            state.payInAmount?.let { viewModel.updateAmount(it) }
        }


        confirmCredentialsAvailability()

        // Set available payment methods
        val availablePaymentKinds = viewModel.state.value.paymentKinds
        val chipPaymentKindList = mutableListOf<Pair<String, Boolean>>()

        availablePaymentKinds.forEach { possiblePaymentKind ->
            chipPaymentKindList.add(
                Pair(possiblePaymentKind.formattedKindName, possiblePaymentKind.isSelected)
            )
        }


        chipAdapter = PaymentMethodChipAdapter { chipClickedText ->
            val previouslySelectedKind = viewModel.state.value.selectedPaymentKind
            val paymentTypeClicked = availablePaymentKinds.find { it.formattedKindName == chipClickedText }

            paymentTypeClicked?.let {
                val bottomSheet = PaymentMethodBottomSheet.newInstance(
                    viewModel,
                    previouslySelectedKind ?: it,
                    previouslySelectedKind
                )

                bottomSheet.show(childFragmentManager, "BOTTOM_SHEET_PAYMENT_METHOD")
            }
        }

        binding.rvPaymentMethods.adapter = chipAdapter

    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    state.paymentKinds.let {
                        val paymentKinds = viewModel.state.value.paymentKinds.map { Pair(it.formattedKindName, it.isSelected) }
                        chipAdapter.updatePaymentKinds(paymentKinds)
                    }

                    state.isQuoteReceived.let {
                        if (it) {
                            binding.btnQuote.text = "Order"
                        } else {
                            binding.btnQuote.text = "Get Quote"
                        }
                    }

                    if (state.selectedPaymentKind == null) {
                        binding.btnQuote.disable(resources)
                    } else {
                        binding.btnQuote.enable(resources)
                    }

                    when(state.btnState) {
                        QuoteButtonState.Disabled -> binding.btnQuote.disable(resources, binding.progressBar)
                        QuoteButtonState.Enabled -> binding.btnQuote.enable(resources, binding.progressBar)
                        QuoteButtonState.Loading -> binding.btnQuote.isLoading(resources, binding.progressBar)
                    }

                    when(val prg = state.exchangeProgress) {
                        is ExchangeProgress.YetToRequestQuote -> {

                        }

                        is ExchangeProgress.HasRequestedQuote -> {
                            requireActivity().showInfoAlerter("Has requested quote")
                        }

                        is ExchangeProgress.HasGottenQuoteResponse -> {
                            requireActivity().showSuccessAlerter("Has gotten quote response") {}
                        }

                        is ExchangeProgress.ErrorRequestingQuote -> {
                            requireActivity().showErrorAlerter(prg.message) {}
                        }

                        is ExchangeProgress.HasMadeOrder -> {

                        }

                        is ExchangeProgress.HasGottenOrderResponse -> {

                        }

                    }
                }
            }
        }
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
        offeringsViewModel.state.value.selectedOffering?.let { offering ->
            offering.data.payin.methods.forEach {
                it.requiredPaymentDetails?.log("PAY IN ----> ")
            }

            offering.data.payout.methods.forEach {
                it.requiredPaymentDetails?.log("PAY OUT ----> ")
            }
        }
    }

    private fun setListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }

            btnQuote.setOnClickListener { viewModel.requestForQuote() }
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
                    meth.kind
                    ("Pay in Method (${off.data.payin.currencyCode} -> ${off.data.payout.currencyCode})- " + meth.kind).log()

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}