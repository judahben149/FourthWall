package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomsheetLayoutPaymentMethodBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayInBankTransferBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayInUsdBankTransferBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayInWalletAddressBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayOutBankTransferBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayOutUsdBankTransferBinding
import com.judahben149.fourthwall.databinding.StubLayoutPayOutWalletAddressBinding
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

    private lateinit var stubPayInWalletAddress: StubLayoutPayInWalletAddressBinding
    private lateinit var stubPayInBankTransfer: StubLayoutPayInBankTransferBinding
    private lateinit var stubPayInUsdBankTransfer: StubLayoutPayInUsdBankTransferBinding
    private lateinit var stubPayOutWalletAddress: StubLayoutPayOutWalletAddressBinding
    private lateinit var stubPayOutBankTransfer: StubLayoutPayOutBankTransferBinding
    private lateinit var stubPayOutUsdBankTransfer: StubLayoutPayOutUsdBankTransferBinding

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

        decideViewToShow()
        prefillData()
        setupListeners()
    }

    private fun prefillData() {
        previouslySelectedKind?.let { paymentKind ->
            when (paymentKind.payInMethod) {
                PaymentMethods.WALLET_ADDRESS.name -> {
                    stubPayInWalletAddress.etPayInWalletAddress.setText(paymentKind.payInWalletAddress)
                }

                PaymentMethods.BANK_TRANSFER.name -> {
                    stubPayInBankTransfer.etPayInBankAccount.setText(paymentKind.payInBankAccount)
                }

                PaymentMethods.BANK_TRANSFER_USD.name -> {
                    stubPayInUsdBankTransfer.etPayInUsdBankAccount.setText(paymentKind.payInBankAccount)
                    stubPayInUsdBankTransfer.etPayInRoutingNumber.setText(paymentKind.payInRoutingNumber)
                }
            }

            when (paymentKind.payOutMethod) {
                PaymentMethods.WALLET_ADDRESS.name -> {
                    stubPayOutWalletAddress.etPayOutWalletAddress.setText(paymentKind.payOutWalletAddress)
                }

                PaymentMethods.BANK_TRANSFER.name -> {
                    stubPayOutBankTransfer.etPayOutBankAccount.setText(paymentKind.payOutBankAccount)
                }

                PaymentMethods.BANK_TRANSFER_USD.name -> {
                    stubPayOutUsdBankTransfer.etPayOutUsdBankAccount.setText(paymentKind.payOutBankAccount)
                    stubPayOutUsdBankTransfer.etPayOutRoutingNumber.setText(paymentKind.payOutRoutingNumber)
                }
            }
        }
    }

    private fun setupListeners() {
        setupTextFieldListeners()

        binding.btnUpdate.setOnClickListener {

            val updatedPaymentKind = parseOutDataIntoPaymentKindObject()
            viewModel.updateSelectedPaymentKind(updatedPaymentKind)
            dismiss()
        }
    }

    private fun setupTextFieldListeners() {
        when (paymentKind.payInMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayInWalletAddress.run {
                    etPayInWalletAddress.doAfterTextChanged { validateInputs() }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayInBankTransfer.run {
                    etPayInBankAccount.doAfterTextChanged { validateInputs() }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayInUsdBankTransfer.run {
                    etPayInUsdBankAccount.doAfterTextChanged { validateInputs() }
                    etPayInRoutingNumber.doAfterTextChanged { validateInputs() }

                }
            }
        }

        when (paymentKind.payOutMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayOutWalletAddress.run {
                    etPayOutWalletAddress.doAfterTextChanged { validateInputs() }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayOutBankTransfer.run {
                    etPayOutBankAccount.doAfterTextChanged { validateInputs() }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayOutUsdBankTransfer.run {
                    etPayOutUsdBankAccount.doAfterTextChanged { validateInputs() }
                    etPayOutRoutingNumber.doAfterTextChanged { validateInputs() }
                }
            }
        }
    }

    private fun parseOutDataIntoPaymentKindObject(): PaymentKind {
        var kind: PaymentKind = paymentKind

        when (paymentKind.payInMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayInWalletAddress.run {
                    etPayInWalletAddress.text.toString().let { text ->
                        kind = kind.copy(payInWalletAddress = text)
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayInBankTransfer.run {
                    etPayInBankAccount.text.toString().let { text ->
                        kind = kind.copy(payInBankAccount = text)
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayInUsdBankTransfer.run {
                    etPayInUsdBankAccount.text.toString().let { payInAccount ->
                        etPayInRoutingNumber.text.toString().let { routingNumber ->
                            kind = kind.copy(
                                payInBankAccount = payInAccount,
                                payInRoutingNumber = routingNumber
                            )
                        }
                    }
                }
            }
        }

        when (paymentKind.payOutMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayOutWalletAddress.run {
                    etPayOutWalletAddress.text.toString().let { text ->
                        kind = kind.copy(payOutWalletAddress = text)
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayOutBankTransfer.run {
                    etPayOutBankAccount.text.toString().let { text ->
                        kind = kind.copy(payOutBankAccount = text)
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayOutUsdBankTransfer.run {
                    etPayOutUsdBankAccount.text.toString().let { payInAccount ->
                        etPayOutRoutingNumber.text.toString().let { routingNumber ->
                            kind = kind.copy(
                                payOutBankAccount = payInAccount,
                                payOutRoutingNumber = routingNumber
                            )
                        }
                    }
                }
            }
        }

        return kind
    }

    private fun decideViewToShow() {
        paymentKind.let {
            when (paymentKind.payInMethod) {
                PaymentMethods.WALLET_ADDRESS.name -> {
                    val inflatedView = binding.stubPayInWalletAddress.inflate()
                    stubPayInWalletAddress = StubLayoutPayInWalletAddressBinding.bind(inflatedView)
                    stubPayInWalletAddress.root.visibility = View.VISIBLE
                }

                PaymentMethods.BANK_TRANSFER.name -> {
                    val inflatedView = binding.stubPayInBankTransfer.inflate()
                    stubPayInBankTransfer = StubLayoutPayInBankTransferBinding.bind(inflatedView)
                    stubPayInBankTransfer.root.visibility = View.VISIBLE
                }

                PaymentMethods.BANK_TRANSFER_USD.name -> {
                    val inflatedView = binding.stubPayInUsdBankTransfer.inflate()
                    stubPayInUsdBankTransfer = StubLayoutPayInUsdBankTransferBinding.bind(inflatedView)
                    stubPayInUsdBankTransfer.root.visibility = View.VISIBLE
                }
            }

            when (paymentKind.payOutMethod) {
                PaymentMethods.WALLET_ADDRESS.name -> {
                    val inflatedView = binding.stubPayOutWalletAddress.inflate()
                    stubPayOutWalletAddress = StubLayoutPayOutWalletAddressBinding.bind(inflatedView)
                    stubPayOutWalletAddress.root.visibility = View.VISIBLE
                }

                PaymentMethods.BANK_TRANSFER.name -> {
                    val inflatedView = binding.stubPayOutBankTransfer.inflate()
                    stubPayOutBankTransfer = StubLayoutPayOutBankTransferBinding.bind(inflatedView)
                    stubPayOutBankTransfer.root.visibility = View.VISIBLE
                }

                PaymentMethods.BANK_TRANSFER_USD.name -> {
                    val inflatedView = binding.stubPayOutUsdBankTransfer.inflate()
                    stubPayOutUsdBankTransfer = StubLayoutPayOutUsdBankTransferBinding.bind(inflatedView)
                    stubPayOutUsdBankTransfer.root.visibility = View.VISIBLE
                }
            }
        }

        //disable button
        binding.btnUpdate.disable(resources)
    }

    private fun validateInputs() {
        var isPayInInputSatisfied = false
        var isPayOutInputSatisfied = false

        when (paymentKind.payInMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayInWalletAddress.run {
                    if (etPayInWalletAddress.text?.isNotBlank() == true) {
                        isPayInInputSatisfied = true
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayInBankTransfer.run {
                    if ((etPayInBankAccount.text?.isNotBlank() == true)) {
                        isPayInInputSatisfied = true
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayInUsdBankTransfer.run {
                    if (
                        (etPayInUsdBankAccount.text?.isNotBlank() == true) &&
                        (etPayInRoutingNumber.text?.isNotBlank() == true)
                    ) {
                        isPayInInputSatisfied = true
                    }
                }
            }
        }

        when (paymentKind.payOutMethod) {
            PaymentMethods.WALLET_ADDRESS.name -> {
                stubPayOutWalletAddress.run {
                    if (etPayOutWalletAddress.text?.isNotBlank() == true) {
                        isPayOutInputSatisfied = true
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER.name -> {
                stubPayOutBankTransfer.run {
                    if ((etPayOutBankAccount.text?.isNotBlank() == true)) {
                        isPayOutInputSatisfied = true
                    }
                }
            }

            PaymentMethods.BANK_TRANSFER_USD.name -> {
                stubPayOutUsdBankTransfer.run {
                    if (
                        (etPayOutUsdBankAccount.text?.isNotBlank() == true) &&
                        (etPayOutRoutingNumber.text?.isNotBlank() == true)
                    ) {
                        isPayOutInputSatisfied = true
                    }
                }
            }
        }


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