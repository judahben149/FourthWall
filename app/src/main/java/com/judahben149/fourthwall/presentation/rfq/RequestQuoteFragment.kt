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
import com.judahben149.fourthwall.domain.models.PaymentMethod
import com.judahben149.fourthwall.presentation.exchange.OfferingsViewModel
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.text.extractPaymentFields
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
    private lateinit var payInChipAdapter: PayInMethodChipAdapter
    private lateinit var payOutChipAdapter: PayOutMethodChipAdapter

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


        payInChipAdapter = PayInMethodChipAdapter { chipClickedText ->
            val bottomSheet = PaymentMethodBottomSheet.newInstance(viewModel, true)
            bottomSheet.show(childFragmentManager, "BOTTOM_SHEET_PAYMENT_METHOD")
        }

        payOutChipAdapter = PayOutMethodChipAdapter { chipClickedText ->
            val bottomSheet = PaymentMethodBottomSheet.newInstance(viewModel, false)
            bottomSheet.show(childFragmentManager, "BOTTOM_SHEET_PAYMENT_METHOD")
        }

        binding.rvPaymentInMethods.adapter = payInChipAdapter
        binding.rvPaymentOutMethods.adapter = payInChipAdapter
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    when (state.btnState) {
                        QuoteButtonState.Disabled -> binding.btnQuote.disable(
                            resources,
                            binding.progressBar
                        )

                        QuoteButtonState.Enabled -> binding.btnQuote.enable(
                            resources,
                            binding.progressBar
                        )

                        QuoteButtonState.Loading -> binding.btnQuote.isLoading(
                            resources,
                            binding.progressBar
                        )
                    }

                    when (val prg = state.exchangeProgress) {
                        is ExchangeProgress.JustStarted -> {
                            binding.btnQuote.disable(resources)
                            binding.btnQuote.text = "Get Quote"
                        }

                        is ExchangeProgress.YetToRequestQuote -> {
                            getAndSetPaymentMethods()
                        }

                        is ExchangeProgress.IsReadyToRequestQuote -> {
                            binding.btnQuote.enable(resources)
                        }

                        is ExchangeProgress.HasRequestedQuote -> {
                            binding.btnQuote.isLoading(resources, binding.progressBar)
                            requireActivity().showInfoAlerter("Has requested quote")
                        }

                        is ExchangeProgress.IsPollingForQuoteResponse -> {

                        }

                        is ExchangeProgress.HasGottenQuoteResponse -> {
                            requireActivity().showSuccessAlerter("Has gotten quote response") {}
                            binding.btnQuote.text = "Order"
                            binding.btnQuote.enable(resources)
                        }

                        is ExchangeProgress.ErrorRequestingQuote -> {
                            binding.btnQuote.text = "Get Quote"
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

    private fun getAndSetPaymentMethods() {
        viewModel.state.value.fwOffering?.let { off ->
            val payInMethods = off.payInMethods.map { it.kind }
            val payOutMethods = off.payOutMethods.map { it.kind }

            payInChipAdapter.updatePaymentKinds(payInMethods)
            payOutChipAdapter.updatePaymentKinds(payOutMethods)
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
                it.requiredPaymentDetails?.fields()?.log("FIELDS ----> ")
            }

            offering.data.payout.methods.forEach { method ->
                method.requiredPaymentDetails?.log("PAY OUT ----> ")

                val kind = method.kind
                val fields =
                    method.requiredPaymentDetails?.let { extractPaymentFields(it) } ?: emptyList()

                val paymentMethod = PaymentMethod(kind, fields)

                paymentMethod.log("PAYMENT METHOD ---> ")
            }
        }
    }

    private fun setListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }

            btnQuote.setOnClickListener {
                // Decide if to request for quote here or make order
                viewModel.requestForQuote()
            }
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