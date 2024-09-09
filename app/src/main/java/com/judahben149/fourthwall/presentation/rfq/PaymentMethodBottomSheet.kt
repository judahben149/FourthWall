package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomsheetLayoutPaymentMethodBinding
import com.judahben149.fourthwall.domain.models.enums.PayOutMethods

class PaymentMethodBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutPaymentMethodBinding? = null
    private val binding get() = _binding!!

    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var viewModel: QuoteViewModel
    private lateinit var payOutMethods: PayOutMethods
    private lateinit var methodDetails: Pair<String, String>

    companion object {
        fun newInstance(
            viewModel: QuoteViewModel,
            method: PayOutMethods,
            methodDetails: Pair<String, String>
        ): PaymentMethodBottomSheet {
            return PaymentMethodBottomSheet().apply {
                this.viewModel = viewModel
                this.payOutMethods = method
                this.methodDetails = methodDetails
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

        setupListeners()
    }

    private fun setupListeners() {
        when (payOutMethods) {
            PayOutMethods.WALLET_ADDRESS -> binding.etWalletAddress.doAfterTextChanged {
                validateInputs(
                    payOutMethods
                )
            }

            PayOutMethods.BANK_TRANSFER -> {
                binding.etBankAccountPayIn.doAfterTextChanged { validateInputs(payOutMethods) }
                binding.etBankAccountPayOut.doAfterTextChanged { validateInputs(payOutMethods) }
            }

            PayOutMethods.BANK_TRANSFER_USD -> {
                binding.etBankAccountPayInUsd.doAfterTextChanged { validateInputs(payOutMethods) }
                binding.etBankAccountPayOutUsd.doAfterTextChanged { validateInputs(payOutMethods) }
                binding.etRoutingNumberUsd.doAfterTextChanged { validateInputs(payOutMethods) }
            }
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.updateSelectedPaymentKind(methodDetails)
            dismiss()
        }
    }

    private fun validateInputs(method: PayOutMethods) {
        when (method) {
            PayOutMethods.WALLET_ADDRESS -> {
                if (binding.etWalletAddress.text?.isNotBlank() == true) {
                    binding.btnUpdate.isEnabled = true
                }
            }

            PayOutMethods.BANK_TRANSFER -> {
                if (
                    (binding.etBankAccountPayIn.text?.isNotBlank() == true) &&
                    (binding.etBankAccountPayOut.text?.isNotBlank() == true)
                ) {
                    binding.btnUpdate.isEnabled = true
                }
            }

            PayOutMethods.BANK_TRANSFER_USD -> {
                if (
                    (binding.etBankAccountPayInUsd.text?.isNotBlank() == true) &&
                    (binding.etBankAccountPayOutUsd.text?.isNotBlank() == true) &&
                    (binding.etRoutingNumberUsd.text?.isNotBlank() == true)
                ) {
                    binding.btnUpdate.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}