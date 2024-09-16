package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomsheetLayoutPaymentMethodBinding
import com.judahben149.fourthwall.utils.text.DynamicTextInputManager
import com.judahben149.fourthwall.utils.text.TextInputConfig
import com.judahben149.fourthwall.utils.text.camelCaseToWords
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutPaymentMethodBinding? = null
    private val binding get() = _binding!!

    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var viewModel: QuoteViewModel
    private var isPayIn: Boolean = true
    private var payKind: String = ""


    companion object {
        fun newInstance(
            viewModel: QuoteViewModel,
            isPayIn: Boolean,
            payKind: String
        ): PaymentMethodBottomSheet {
            return PaymentMethodBottomSheet().apply {
                this.viewModel = viewModel
                this.isPayIn = isPayIn
                this.payKind = payKind
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetLayoutPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        inflateViews()
    }

    private fun inflateViews() {
        binding.btnUpdate.disable(resources)

        val container = binding.layoutPayIn
        val textManager = DynamicTextInputManager(requireContext(), resources)

        viewModel.state.value.fwOffering?.let { it ->

            val payInConfigs = it.payInMethods.map { method ->
                method.paymentFields
            }.flatten().map { field ->
                TextInputConfig(field.fieldName, camelCaseToWords(field.fieldName))
            }

            val payOutConfigs = it.payOutMethods.map { method ->
                method.paymentFields
            }.flatten().map { field ->
                TextInputConfig(field.fieldName, camelCaseToWords(field.fieldName))
            }


            if (isPayIn) {
                if (payInConfigs.isNotEmpty()) {
                    textManager.createTextInputs(
                        container,
                        payInConfigs,
                        binding.btnUpdate
                    ) { detailsMap ->
                        viewModel.updateRequestedDetailsFields(isPayIn, payKind, detailsMap)
                        dismiss()
                    }
                } else {
                    binding.run {
                        listOf(layoutPayIn, layoutPayOut, btnUpdate, spacer).forEach {
                            it.visibility = View.GONE
                        }

                        tvNoRequiredFields.visibility = View.VISIBLE
                    }
                }
            } else {

                if (payOutConfigs.isNotEmpty()) {
                    textManager.createTextInputs(
                        container,
                        payOutConfigs,
                        binding.btnUpdate
                    ) { detailsMap ->
                        viewModel.updateRequestedDetailsFields(isPayIn, payKind, detailsMap)
                        dismiss()
                    }
                } else {
                    binding.run {
                        listOf(layoutPayIn, layoutPayOut, btnUpdate, spacer).forEach {
                            it.visibility = View.GONE
                        }

                        tvNoRequiredFields.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}