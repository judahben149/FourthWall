package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomsheetLayoutPaymentMethodBinding
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutPaymentMethodBinding? = null
    private val binding get() = _binding!!

    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var viewModel: QuoteViewModel
    private lateinit var paymentKind: PaymentKind
    private var previouslySelectedKind: PaymentKind? = null

    companion object {
        fun newInstance(
            viewModel: QuoteViewModel,
            paymentKind: PaymentKind,
            previouslySelectedKind: PaymentKind?
        ): PaymentMethodBottomSheet {
            return PaymentMethodBottomSheet().apply {
                this.viewModel = viewModel
                this.paymentKind = paymentKind
                this.previouslySelectedKind = previouslySelectedKind
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

        prefillData()
        setupListeners()
        decideViewToShow()
    }

    private fun prefillData() {
        previouslySelectedKind?.let {
            when(paymentKind.kind) {
                PaymentMethods.WALLET_ADDRESS ->  {
                    binding.etWalletAddress.setText(paymentKind.walletAddress)
                }

                PaymentMethods.BANK_TRANSFER -> {
                    binding.etBankAccountPayIn.setText(paymentKind.bankAccountPayIn)
                    binding.etBankAccountPayOut.setText(paymentKind.bankAccountPayOut)
                }

                PaymentMethods.BANK_TRANSFER_USD -> {
                    binding.etBankAccountPayInUsd.setText(paymentKind.bankAccountPayInUsd)
                    binding.etBankAccountPayOutUsd.setText(paymentKind.bankAccountPayOutUsd)
                    binding.etRoutingNumberUsd.setText(paymentKind.routingNumber)
                }
            }
        }
    }

    private fun setupListeners() {
        when (paymentKind.kind) {
            PaymentMethods.WALLET_ADDRESS -> binding.etWalletAddress.doAfterTextChanged {
                validateInputs(paymentKind.kind)
            }

            PaymentMethods.BANK_TRANSFER -> {
                binding.etBankAccountPayIn.doAfterTextChanged { validateInputs(paymentKind.kind) }
                binding.etBankAccountPayOut.doAfterTextChanged { validateInputs(paymentKind.kind) }
            }

            PaymentMethods.BANK_TRANSFER_USD -> {
                binding.etBankAccountPayInUsd.doAfterTextChanged { validateInputs(paymentKind.kind) }
                binding.etBankAccountPayOutUsd.doAfterTextChanged { validateInputs(paymentKind.kind) }
                binding.etRoutingNumberUsd.doAfterTextChanged { validateInputs(paymentKind.kind) }
            }
        }

        binding.btnUpdate.setOnClickListener {
            when (paymentKind.kind) {
                PaymentMethods.WALLET_ADDRESS ->  {
                    val address = binding.etWalletAddress.text.toString()

                    paymentKind = paymentKind.copy(
                        walletAddress = address
                    )
                }

                PaymentMethods.BANK_TRANSFER -> {
                    val bankAccountPayIn = binding.etBankAccountPayIn.text.toString()
                    val bankAccountPayOut = binding.etBankAccountPayOut.text.toString()

                    paymentKind = paymentKind.copy(
                        bankAccountPayIn = bankAccountPayIn,
                        bankAccountPayOut = bankAccountPayOut
                    )
                }

                PaymentMethods.BANK_TRANSFER_USD -> {
                    val bankAccountPayInUsd = binding.etBankAccountPayInUsd.text.toString()
                    val bankAccountPayOutUsd = binding.etBankAccountPayOutUsd.text.toString()
                    val routingNumber = binding.etRoutingNumberUsd.text.toString()

                    paymentKind = paymentKind.copy(
                        bankAccountPayInUsd = bankAccountPayInUsd,
                        bankAccountPayOutUsd = bankAccountPayOutUsd,
                        routingNumber = routingNumber
                    )
                }
            }

            viewModel.updateSelectedPaymentKind(paymentKind)
            dismiss()
        }
    }

    private fun decideViewToShow() {
        when (paymentKind.kind) {
            PaymentMethods.WALLET_ADDRESS -> {
                binding.layoutWalletAddress.visibility = View.VISIBLE
            }

            PaymentMethods.BANK_TRANSFER -> {
                binding.layoutBankAccount.visibility = View.VISIBLE
            }

            PaymentMethods.BANK_TRANSFER_USD -> {
                binding.layoutBankAccountUsd.visibility = View.VISIBLE
            }
        }
        
        //disable button
        binding.btnUpdate.disable(resources)
    }

    private fun validateInputs(method: PaymentMethods) {
        when (method) {
            PaymentMethods.WALLET_ADDRESS -> {
                if (binding.etWalletAddress.text?.isNotBlank() == true) {
                    enableBtn()
                } else {
                    disableBtn()
                }
            }

            PaymentMethods.BANK_TRANSFER -> {
                if (
                    (binding.etBankAccountPayIn.text?.isNotBlank() == true) &&
                    (binding.etBankAccountPayOut.text?.isNotBlank() == true)
                ) {
                    enableBtn()
                } else {
                    disableBtn()
                }
            }

            PaymentMethods.BANK_TRANSFER_USD -> {
                if (
                    (binding.etBankAccountPayInUsd.text?.isNotBlank() == true) &&
                    (binding.etBankAccountPayOutUsd.text?.isNotBlank() == true) &&
                    (binding.etRoutingNumberUsd.text?.isNotBlank() == true)
                ) {
                    enableBtn()
                } else {
                    disableBtn()
                }
            }
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