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
        decideViewToShow()
        setupListeners()
    }

    private fun inflateViews() {
        val container = binding.layoutPayIn
        val textManager = DynamicTextInputManager(requireContext(), resources)

        viewModel.state.value.fwOffering?.let {

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
                textManager.createTextInputs(container, payInConfigs, binding.btnUpdate) { detailsMap ->
                    viewModel.updateRequestedDetailsFields(isPayIn, payKind, detailsMap)
                    dismiss()
                }
            } else {
                textManager.createTextInputs(container, payOutConfigs, binding.btnUpdate) { detailsMap ->
                    viewModel.updateRequestedDetailsFields(isPayIn, payKind, detailsMap)
                    dismiss()
                }
            }
        }
    }

    private fun setupListeners() {
        setupTextFieldListeners()

//        binding.btnUpdate.setOnClickListener {
////            viewModel.updateSelectedPaymentKind(updatedPaymentKind)
//            dismiss()
//        }

        //
//        Button listener has been set up in event input lambda
    }

    private fun setupTextFieldListeners() {

    }

    private fun decideViewToShow() {


        //disable button
        binding.btnUpdate.disable(resources)
    }

    private fun validateInputs() {
        var isPayInInputSatisfied = false
        var isPayOutInputSatisfied = false


        if (isPayInInputSatisfied && isPayOutInputSatisfied) {
            enableBtn()
        } else {
            disableBtn()
        }
    }

    private fun enableBtn() {
        binding.btnUpdate.enable(resources)
    }

    private fun disableBtn() {
        binding.btnUpdate.disable(resources)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}