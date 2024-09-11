package com.judahben149.fourthwall.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomSheetCurrencySelectorBinding
import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.presentation.exchange.CurrencyAdapter
import com.judahben149.fourthwall.utils.CurrencyUtils.currencies

class CurrencySelectorBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCurrencySelectorBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetCurrencySelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerViewCurrencies
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = CurrencyAdapter(requireContext()) { currency, countryFlag ->
            viewModel.setCurrency(currency.code, countryFlag)
            dismiss()
        }
        recyclerView.adapter = adapter

        val currencies = currencies.map {
            Currency(it.first, it.second)
        }

        adapter.updateCurrencies(currencies)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(viewModel: AccountViewModel): CurrencySelectorBottomSheet {
            return CurrencySelectorBottomSheet().apply {
                this.viewModel = viewModel
            }
        }
    }
}