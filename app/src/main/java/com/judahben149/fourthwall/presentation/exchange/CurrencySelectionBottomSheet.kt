package com.judahben149.fourthwall.presentation.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.BottomsheetLayoutCurrencySelectionBinding
import com.judahben149.fourthwall.domain.models.Currency

class CurrencySelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutCurrencySelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var currencyAdapter: CurrencyAdapter
    private var onCurrencySelected: ((Currency) -> Unit)? = null

    fun setOnCurrencySelectedListener(listener: (Currency) -> Unit) {
        onCurrencySelected = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = BottomsheetLayoutCurrencySelectionBinding.inflate(inflater, container, false)
//        return binding.root
        return inflater.inflate(R.layout.bottomsheet_layout_currency_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

//    fun updateCurrencies(currencies: List<Currency>) {
//        view?.let {
//            recyclerView = it.findViewById(R.id.rvCurrencies)
//
//            currencyAdapter = CurrencyAdapter (re){ currency ->
//                onCurrencySelected?.invoke(currency)
//                dismiss()
//            }
//
//            binding.rvCurrencies.adapter = currencyAdapter
//            currencyAdapter.updateCurrencies(currencies)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CurrencySelectionBottomSheet"
    }
}