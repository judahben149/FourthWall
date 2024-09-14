package com.judahben149.fourthwall.presentation.exchange

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOfferingsBinding
import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.utils.CurrencyUtils
import com.judahben149.fourthwall.utils.text.DecimalDigitsInputFilter
import com.judahben149.fourthwall.utils.views.animateBorderColorForError
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.setAmountFont
import com.judahben149.fourthwall.utils.views.showInfoAlerter
import com.judahben149.fourthwall.utils.views.showSnack
import com.judahben149.fourthwall.utils.views.showWarningAlerter
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OfferingsFragment : Fragment() {

    private var _binding: FragmentOfferingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OfferingsViewModel by hiltNavGraphViewModels(R.id.order_flow_nav)
    private val navController by lazy { findNavController() }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var otherOfferingsAdapter: OtherOfferingsAdapter
    private lateinit var currentCurrencyType: CurrencyType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.offeringsFragment) {
                    navController.popBackStack(R.id.order_flow_nav, true, false)
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shimmerLayout.startShimmer()
        requireActivity().showInfoAlerter("Finding available offerings", 800)

        setTextFieldFilters()
        setupBottomSheet()
        setupBottomSheetRecyclerView()
        observeState()
        setupListeners()
        recallData()
    }

    private fun setupListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.popBackStack(R.id.order_flow_nav, true, false) }

            // This is to increase the touch target area
            ivSelectPayInCurrency.setOnClickListener { setPayInCurrency() }
            tvCurrencyPayIn.setOnClickListener { setPayInCurrency() }
            ivFlagPayIn.setOnClickListener { setPayInCurrency() }

            ivSelectPayOutCurrency.setOnClickListener { setPayOutCurrency() }
            tvCurrencyPayOut.setOnClickListener { setPayOutCurrency() }
            ivFlagPayOut.setOnClickListener { setPayOutCurrency() }

            btnContinue.setOnClickListener {
                navController.navigate(R.id.action_offeringsFragment_to_requestQuoteFragment)
            }

            chipExplorePfi.setOnClickListener {
                exploreOtherOfferings()
            }

            etPayIn.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val payInAmount = it.toString()
                        viewModel.updatePayInAmount(payInAmount)
                    }
                }
            })
        }
    }

    private fun exploreOtherOfferings() {
        if (viewModel.state.value.offeringsList.isNotEmpty()) {
            clearEditTextFocus()
            showBottomSheetForOfferings()
        }
    }

    private fun setPayInCurrency() {
        clearEditTextFocus()
        currentCurrencyType = CurrencyType.PayIn

        val payInCurrencies = viewModel.state.value.supportedCurrencyPairs.map { it.first }.distinct()
        showCurrencyBottomSheetForCurrencies(payInCurrencies)
    }

    private fun setPayOutCurrency() {
        clearEditTextFocus()
        currentCurrencyType = CurrencyType.PayOut
        val selectedPayInCurrency = viewModel.state.value.selectedPayInCurrency

        selectedPayInCurrency?.let {
            val payOutCurrencies = viewModel.state.value.supportedCurrencyPairs.filter { currencyPair ->
                currencyPair.first.code == selectedPayInCurrency.code
            }.map { it.second }.distinct()

            showCurrencyBottomSheetForCurrencies(payOutCurrencies)
        } ?: requireActivity().showWarningAlerter("Select pay in currency", 1300) {}
    }

    private fun setupBottomSheetRecyclerView() {
        currencyAdapter = CurrencyAdapter(requireContext()) { currency, countryFlag ->
            clearPayOutCurrencyDetails()

            when (currentCurrencyType) {
                CurrencyType.PayIn -> {
                    viewModel.updateSelectedPayInCurrency(currency.code)

                    updateViewWithCurrencyInfo(
                        currency.code,
                        countryFlag,
                        binding.tvCurrencyPayIn,
                        binding.ivFlagPayIn
                    )

                }
                CurrencyType.PayOut -> {
                    viewModel.updateSelectedPayOutCurrency(currency.code)

                    updateViewWithCurrencyInfo(
                        currency.code,
                        countryFlag,
                        binding.tvCurrencyPayOut,
                        binding.ivFlagPayOut
                    )
                }
            }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        otherOfferingsAdapter = OtherOfferingsAdapter(requireContext()) { offeringId ->
            viewModel.updateSelectedOffering(offeringId)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        binding.rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrencies.adapter = currencyAdapter

        binding.rvOtherOfferings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOtherOfferings.adapter = otherOfferingsAdapter
    }

    private fun recallData() {
        viewModel.state.value.run {
            selectedPayInCurrency?.let {
                val countryFlag = CurrencyUtils.getCountryFlag(requireContext(), it.code)

                updateViewWithCurrencyInfo(
                    it.code,
                    countryFlag,
                    binding.tvCurrencyPayIn,
                    binding.ivFlagPayIn
                )
            }

            selectedPayOutCurrency?.let {
                val countryFlag = CurrencyUtils.getCountryFlag(requireContext(), it.code)

                updateViewWithCurrencyInfo(
                    it.code,
                    countryFlag,
                    binding.tvCurrencyPayOut,
                    binding.ivFlagPayOut
                )
            }
        }
    }

    private fun updateViewWithCurrencyInfo(
        currencyCode: String,
        @DrawableRes countryFlag: Int?,
        tv: MaterialTextView,
        iv: ImageView
    ) {
        tv.text = currencyCode

        Glide.with(iv)
            .load(countryFlag)
            .into(iv)
    }

    private fun clearPayOutCurrencyDetails() {
        binding.run {
            ivFlagPayOut.setImageDrawable(null)
            tvCurrencyPayOut.text = ""
        }

        viewModel.updateSelectedPayOutCurrency(null)
        viewModel.refreshPayOutInfo()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Set up the scrim
        binding.scrim.alpha = 0f
        binding.scrim.visibility = View.GONE

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.scrim.animate().alpha(0f).setDuration(100).withEndAction {
                            binding.scrim.visibility = View.GONE
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if ((binding.scrim.visibility == View.VISIBLE).not()) {
                            binding.scrim.visibility = View.VISIBLE
                            binding.scrim.animate().alpha(0.6f).setDuration(100)
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // Set up click listener for the scrim to dismiss the bottom sheet
        binding.scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setTextFieldFilters() {
        binding.run {
            val textFields = listOf(this.etPayIn, this.tvPayOut)

            textFields.forEach {
                it.filters = arrayOf(
                    DecimalDigitsInputFilter(10, 2),
                    InputFilter.LengthFilter(13)
                )
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when(state.btnState) {
                        is OfferingsBtnState.Loading -> {
                            binding.btnContinue.isLoading(resources, binding.progressBar)
                        }

                        is OfferingsBtnState.Disabled -> {
                            binding.btnContinue.disable(resources, binding.progressBar)
                        }

                        is OfferingsBtnState.Enabled -> {
                            binding.btnContinue.enable(resources, binding.progressBar)
                        }
                    }

                    when(state.selectedOffering) {
                        null -> {
                            binding.run {
                                tvPfi.text = "--"
                                chipExplorePfi.isEnabled = false
                            }
                        }
                    }

                    when(state.getOfferingsState) {
                        is GetOfferingsRequestState.Error -> {

                        }

                        GetOfferingsRequestState.Loading -> {

                        }

                        GetOfferingsRequestState.Success -> {
                            binding.shimmerLayout.apply {
                                stopShimmer()
                                visibility = View.GONE
                            }

                            binding.layoutPfiContent.visibility = View.VISIBLE
                        }
                    }

                    when(val payoutState = state.payOutAmountState) {
                        is PayOutAmountState.Available -> {
                            binding.tvPayOut.apply {
                                setAmountFont(requireContext())
                                text = payoutState.amount
                            }
                        }

                        is PayOutAmountState.Error -> {
                            requireActivity().showInfoAlerter(payoutState.message)
                            binding.layoutPayIn.animateBorderColorForError()
                        }

                        is PayOutAmountState.Inactive -> {
                            binding.tvPayOut.apply {
                                setAmountFont(requireContext())
                                text = payoutState.message
                            }
                        }
                    }

                    when(state.isBestOfferSelected) {
                        null -> {
                            // Hasn't gotten any offers yet
                            binding.tvOnlyOffer.visibility = View.GONE
                            binding.tvBestOffer.visibility = View.GONE

                            binding.tvProvider.visibility = View.INVISIBLE
                        }

                        false -> {
                            // Auto-selected offer is the only one available
                            val pfiName = viewModel.getSelectedPfiName()

                            pfiName?.let {  pfi ->
                                binding.tvOnlyOffer.visibility = View.VISIBLE
                                binding.tvProvider.visibility = View.VISIBLE
                                binding.layoutPfiContent.visibility = View.VISIBLE

                                binding.tvPfi.text = pfi
                                binding.chipExplorePfi.isEnabled = true
                            }
                        }

                        true -> {
                            // Auto-selected offer is the best one
                            val pfiName = viewModel.getSelectedPfiName()

                            pfiName?.let {  pfi ->
                                binding.tvBestOffer.visibility = View.VISIBLE
                                binding.tvProvider.visibility = View.VISIBLE
                                binding.layoutPfiContent.visibility = View.VISIBLE

                                binding.tvPfi.text = pfi
                                binding.chipExplorePfi.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCurrencyBottomSheetForCurrencies(currencies: List<Currency>) {
        binding.rvOtherOfferings.visibility = View.GONE
        binding.rvCurrencies.visibility = View.VISIBLE
        binding.tvBSTitle.text = "Select Currency"

        startAnimatingScrim()

        currencyAdapter.updateCurrencies(currencies)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showBottomSheetForOfferings() {
        binding.rvCurrencies.visibility = View.GONE
        binding.rvOtherOfferings.visibility = View.VISIBLE
        binding.tvBSTitle.text = "Explore offerings"

        startAnimatingScrim()

        viewLifecycleOwner.lifecycleScope.launch {
            val pfiAndOfferingsPair = viewModel.pairOfferingsWithPfiNames()
            val pfiRatings = viewModel.getAveragePfiRating()
            otherOfferingsAdapter.updateOfferings(pfiAndOfferingsPair, pfiRatings)
        }

        bottomSheetBehavior.apply {
            skipCollapsed = true
            expandedOffset = (resources.displayMetrics.heightPixels * 0.3).toInt()
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun showCurrencyToolTip() {
        val balloon = Balloon.Builder(requireContext())
//            .setWidthRatio(1.0f)
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("Edit your profile here!")
            .setTextColorResource(R.color.white)
            .setTextTypeface(Typeface.BOLD)
            .setTextSize(14f)
            .setIconDrawableResource(R.drawable.ic_dollar)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(8)
            .setCornerRadius(8f)
            .setIsVisibleArrow(true)
            .setBackgroundColorResource(R.color.base_purple)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(viewLifecycleOwner)
            .setPreferenceName("CurrencySelectionToolTip")
            .setShowCounts(3)
            .build()

        binding.ivSelectPayInCurrency

    }

    private fun startAnimatingScrim() {
        binding.scrim.visibility = View.VISIBLE
        binding.scrim.animate().alpha(0.6f).setDuration(100)
    }

    private fun clearEditTextFocus() {
        binding.etPayIn.clearFocus()

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etPayIn.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

enum class CurrencyType {
    PayIn, PayOut
}