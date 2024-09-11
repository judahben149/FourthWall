package com.judahben149.fourthwall.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.databinding.FragmentCreateCurrencyAccountBinding
import com.judahben149.fourthwall.utils.CurrencyUtils.getCurrencyName
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.showInfoAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateCurrencyAccountFragment : Fragment() {


    private var _binding: FragmentCreateCurrencyAccountBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val viewModel: AccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCurrencyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }

            layoutCurrency.setOnClickListener {
                val bottomSheet = CurrencySelectorBottomSheet.newInstance(viewModel)
                bottomSheet.show(childFragmentManager, "CURRENCY_BOTTOM_SHEET_SELECTION")
            }

            btnContinue.setOnClickListener {
                viewModel.createCurrencyAccount()
                requireActivity().showSuccessAlerter("Account created successfully") {
                    navController.navigateUp()
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    state.selectedCurrency?.let {
                        binding.tvCurrency.text = getCurrencyName(it)

                        Glide.with(binding.ivFlag)
                            .load(state.countryFlag)
                            .into(binding.ivFlag)
                    }

                    if (state.isCurrencySelected) {
                        binding.btnContinue.enable(resources)
                    } else {
                        binding.btnContinue.disable(resources)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}