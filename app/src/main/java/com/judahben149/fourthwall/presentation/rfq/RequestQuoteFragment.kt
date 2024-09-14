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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentRequestQuoteBinding
import com.judahben149.fourthwall.domain.models.PaymentMethod
import com.judahben149.fourthwall.presentation.exchange.OfferingsViewModel
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.text.extractPaymentFields
import com.judahben149.fourthwall.utils.toCasualFriendlyDate
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showInfoAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import com.judahben149.fourthwall.utils.views.showWarningAlerter
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
        confirmCredentialsAvailability()

//        createRfq()
//        seeSelectedOffering()
    }

    private fun setData() {
        offeringsViewModel.state.value.let { state ->
            state.selectedOffering?.let { viewModel.updateSelectedOffering(it) }
            state.payInAmount?.let { viewModel.updateAmount(it) }
        }

        //if pay in methods are empty, simply add in the pay in kind name
        // You should come back to make this selectable by the user, using the chip
        viewModel.state.value.fwOffering?.let { off ->
            val payInMethodsAreEmpty = off.payInMethods.all { it.paymentFields.isEmpty() }

            if (payInMethodsAreEmpty) {
                viewModel.updatePayKindNameForFieldsNotRequired(off.payInMethods[0].kind)
            }
        }


        payInChipAdapter = PayInMethodChipAdapter { payInKind ->
            val bottomSheet = PaymentMethodBottomSheet.newInstance(viewModel, true, payInKind)
            bottomSheet.show(childFragmentManager, "BOTTOM_SHEET_PAYMENT_METHOD")
        }

        payOutChipAdapter = PayOutMethodChipAdapter { payOutKind ->
            val bottomSheet = PaymentMethodBottomSheet.newInstance(viewModel, false, payOutKind)
            bottomSheet.show(childFragmentManager, "BOTTOM_SHEET_PAYMENT_METHOD_2")
        }

        binding.rvPaymentInMethods.adapter = payInChipAdapter
        binding.rvPaymentOutMethods.adapter = payOutChipAdapter
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    when (val prg = state.exchangeProgress) {
                        is ExchangeProgress.JustStarted -> {
                            binding.btnQuote.text = getString(R.string.get_quote)
                            binding.btnQuote.disable(resources)
                            confirmCredentialsAvailability()
                        }

                        is ExchangeProgress.ErrorGettingCredentials -> {

                        }

                        ExchangeProgress.HasGottenCredentials -> {
                            confirmCredentialsAvailability()
                        }

                        ExchangeProgress.HasRequestedCredentials -> {

                        }

                        is ExchangeProgress.YetToRequestQuote -> {
                            getAndSetPaymentMethods()
                        }

                        is ExchangeProgress.IsReadyToRequestQuote -> {
                            binding.btnQuote.enable(resources, binding.progressBar)
                        }

                        is ExchangeProgress.HasRequestedQuote -> {
                            binding.btnQuote.isLoading(resources, binding.progressBar)
                            requireActivity().showInfoAlerter("Has requested quote")
                        }

                        is ExchangeProgress.IsPollingForQuoteResponse -> {

                        }

                        is ExchangeProgress.HasGottenQuoteResponse -> {
                            requireActivity().showSuccessAlerter("Quote received. See fee breakdown") {}
                            binding.btnQuote.enable(resources, binding.progressBar)

                            binding.run {
                                btnQuote.text = getString(R.string.order)
                                btnQuote.enable(resources)

                                btnCancel.visibility = View.VISIBLE
                                cardFeeBreakdown.visibility = View.VISIBLE

                                state.tbDexQuote?.let { quote ->
                                    if (quote.data.payout.fee.isNullOrEmpty().not()) {
                                        tvFees.text = quote.data.payout.fee

                                        listOf(tvFeesLabel, tvFees, dotted1, dotted2).forEach {
                                            it.visibility = View.VISIBLE
                                        }
                                    }

                                    tvOrderExpires.text =
                                        quote.data.expiresAt.toCasualFriendlyDate()
                                }
                            }
                        }

                        is ExchangeProgress.ErrorRequestingQuote -> {
                            binding.btnQuote.text = getString(R.string.get_quote)
                            requireActivity().showWarningAlerter(prg.message) {}
                        }

                        is ExchangeProgress.IsProcessingOrderRequest -> {
                            requireActivity().showInfoAlerter("FourthWall is processing your order", 1600)
                        }

                        is ExchangeProgress.HasGottenNewOrderStatusMessage -> {
                            requireActivity().showErrorAlerter("Status - ".plus(prg.message), 1000) {}

                        }

                        is ExchangeProgress.HasGottenSuccessfulOrderResponse -> {
                            requireActivity().showSuccessAlerter("Order Fulfilled") {
                                viewModel.canSafelyNavigateAway()
                            }
                        }

                        is ExchangeProgress.ErrorProcessingOrderMessage -> {
                            requireActivity().showErrorAlerter(prg.message) {}
                        }

                        is ExchangeProgress.HasSentCloseMessage -> {

                        }

                        is ExchangeProgress.HasGottenCloseResponse -> {

                        }

                        is ExchangeProgress.ErrorProcessingCloseMessage -> {
                            requireActivity().showErrorAlerter(prg.message) {}
                        }

                        is ExchangeProgress.CanSafeNavigateAway -> {
                            val orderResultSerialized = Gson().toJson(prg.orderResult)
                            val bundle = Bundle().apply { putString("orderResult", orderResultSerialized) }

                            navController.navigate(
                                R.id.orderResultFragment, bundle, NavOptions.Builder()
                                .setPopUpTo(R.id.homeFragment, false)
                                .build())
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
            offering.log("OFFERING -----> ")
            offering.data.payin.methods.forEach { method ->
//                it.requiredPaymentDetails?.fields()?.log("FIELDS ----> ")
                method.requiredPaymentDetails?.log("PAY IN ----> ")

                val kind = method.kind
                val fields =
                    method.requiredPaymentDetails?.let { extractPaymentFields(it) } ?: emptyList()

                val paymentMethod = PaymentMethod(kind, fields)

                paymentMethod.log("PAYMENT IN METHOD ---> ")
            }

            offering.data.payout.methods.forEach { method ->
//                method.requiredPaymentDetails?.log("PAY OUT ----> ")

                val kind = method.kind
                val fields =
                    method.requiredPaymentDetails?.let { extractPaymentFields(it) } ?: emptyList()

                val paymentMethod = PaymentMethod(kind, fields)

                paymentMethod.log("PAYMENT OUT METHOD ---> ")
            }
        }
    }

    private fun setListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }

            btnQuote.setOnClickListener {
                // Decide if to request for quote here or make order
                if (viewModel.state.value.exchangeProgress is ExchangeProgress.HasGottenQuoteResponse) {
                    viewModel.processOrderRequest()
                } else {
                    viewModel.requestForQuote()
                }
            }

            btnCancel.setOnClickListener { viewModel.processCloseRequest() }

            btnRequestCredentials.setOnClickListener {
                val credBottomSheet = GetCredentialsBottomSheet.newInstance(viewModel)
                credBottomSheet.show(childFragmentManager, "BOTTOM_SHEET_GET_CREDENTIALS")
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