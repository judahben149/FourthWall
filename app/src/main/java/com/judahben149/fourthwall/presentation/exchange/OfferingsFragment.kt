package com.judahben149.fourthwall.presentation.exchange

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOfferingsBinding
import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.utils.text.DecimalDigitsInputFilter
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.setAmountFont
import com.judahben149.fourthwall.utils.views.setErrorFont
import com.judahben149.fourthwall.utils.views.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OfferingsFragment : Fragment() {

    private var _binding: FragmentOfferingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OfferingsViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var currentCurrencyType: CurrencyType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextFieldFilters()
        setupBottomSheet()
        setupBottomSheetRecyclerView()
        observeState()
        setupListeners()
    }

    private fun setupListeners() {
        binding.ivSelectPayInCurrency.setOnClickListener {
            clearEditTextFocus()
            currentCurrencyType = CurrencyType.PayIn
            val payInCurrencies = viewModel.state.value.supportedCurrencyPairs.map { it.first }.distinct()
            showCurrencyBottomSheet(payInCurrencies)
        }

        binding.ivSelectPayOutCurrency.setOnClickListener {
            clearEditTextFocus()
            currentCurrencyType = CurrencyType.PayOut
            val selectedPayInCurrency = viewModel.state.value.selectedPayInCurrency

            selectedPayInCurrency?.let {
                val payOutCurrencies = viewModel.state.value.supportedCurrencyPairs.filter { currencyPair ->
                    currencyPair.first.code == selectedPayInCurrency.code
                }.map { it.second }.distinct()

                showCurrencyBottomSheet(payOutCurrencies)
            } ?: showSnack("Select pay in currency", binding.root)
        }

        binding.etPayIn.addTextChangedListener(object : TextWatcher {
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

    private fun setupBottomSheetRecyclerView() {
        currencyAdapter = CurrencyAdapter(requireContext()) { currency, countryFlag ->
            clearPayOutCurrencyDetails()

            when (currentCurrencyType) {
                CurrencyType.PayIn -> {
                    viewModel.updateSelectedPayInCurrency(currency.code)

                    binding.tvCurrencyPayIn.text = currency.code
                    Glide.with(binding.ivFlagPayIn)
                        .load(countryFlag)
                        .into(binding.ivFlagPayIn)

                }
                CurrencyType.PayOut -> {
//                    viewModel.invalidateCurrencySelection()
                    viewModel.updateSelectedPayOutCurrency(currency.code)

                    binding.tvCurrencyPayOut.text = currency.code
                    Glide.with(binding.ivFlagPayOut)
                        .load(countryFlag)
                        .into(binding.ivFlagPayOut)
                }
            }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        binding.rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrencies.adapter = currencyAdapter
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

                    when(val payoutState = state.payOutAmountState) {
                        is PayOutAmountState.Available -> {
                            binding.tvPayOut.apply {
                                setAmountFont(requireContext())
                                text = payoutState.amount
                            }
                        }

                        is PayOutAmountState.Error -> {
                            binding.tvPayOut.apply {
                                setErrorFont(requireContext())
                                text = payoutState.message
                            }
                        }

                        is PayOutAmountState.Inactive -> {
                            binding.tvPayOut.apply {
                                setAmountFont(requireContext())
                                text = payoutState.message
                            }
                        }
                    }

                    when(state.isBestOfferAvailable) {
                        null -> {
                            binding.layoutBestOffer.visibility = View.GONE
                            binding.layoutOnlyOffer.visibility = View.GONE
                        }

                        false -> {
                            val pfiName = viewModel.getSelectedPfiName()

                            pfiName?.let {  pfi ->
                                binding.layoutOnlyOffer.visibility = View.VISIBLE

                                binding.tvPfiNameOnlyOffer.apply {
                                    text = pfi
                                    setTextColor(ContextCompat.getColor(context, R.color.black))
                                }
                            }
                        }

                        true -> {
                            val pfiName = viewModel.getSelectedPfiName()

                            pfiName?.let {  pfi ->
                                binding.layoutBestOffer.visibility = View.VISIBLE

                                binding.tvPfiNameBestOffer.apply {
                                    text = pfi
                                    setTextColor(ContextCompat.getColor(context, R.color.green_offers))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCurrencyBottomSheet(currencies: List<Currency>) {
        startAnimatingScrim()
        currencyAdapter.updateCurrencies(currencies)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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