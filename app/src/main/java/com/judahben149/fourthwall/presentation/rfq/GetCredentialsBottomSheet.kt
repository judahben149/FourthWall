package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judahben149.fourthwall.databinding.BottomsheetLayoutGetCredentialsBinding
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GetCredentialsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutGetCredentialsBinding? = null
    private val binding get() = _binding!!

    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var viewModel: QuoteViewModel


    companion object {
        fun newInstance(
            viewModel: QuoteViewModel,
        ): GetCredentialsBottomSheet {
            return GetCredentialsBottomSheet().apply {
                this.viewModel = viewModel
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetLayoutGetCredentialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        inflateViews()
        observeState()
        setupListeners()
    }

    private fun inflateViews() {
        viewModel.state.value.fwOffering?.let {
            binding.run {
                tvCredentialsLabel.text = tvCredentialsLabel.text.toString() + it.pfiName
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {

                    when(val prg = it.exchangeProgress) {
                        is ExchangeProgress.HasRequestedCredentials -> {
                            binding.btnRequest.isLoading(resources, binding.progressBar)
                        }

                        is ExchangeProgress.HasGottenCredentials -> {
                            requireActivity().showSuccessAlerter("Credentials gotten successfully") {}
                            dismiss()
                        }

                        is ExchangeProgress.ErrorGettingCredentials -> {
                            requireActivity().showErrorAlerter(prg.message) {
                                binding.btnRequest.enable(resources, binding.progressBar)
                            }
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnRequest.setOnClickListener {
            binding.btnRequest.isLoading(resources, binding.progressBar)
            viewModel.getKCC()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}