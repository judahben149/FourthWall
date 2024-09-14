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
import com.judahben149.fourthwall.databinding.FragmentFundWalletBinding
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.CurrencyUtils.getCountryFlag
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FundWalletFragment : Fragment() {

    private var _binding: FragmentFundWalletBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFundWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }

            viewModel.getCurrencyCode(Constants.currencyAccountId) { currencyCode ->
                btnFund.setOnClickListener {
                        val amount = binding.etFundAccount.text.toString().toDouble()
                        viewModel.fundAccount(Constants.currencyAccountId, amount, currencyCode)
                }

                getCountryFlag(requireContext(), currencyCode)?.let {
                    binding.run {
                        tvCurrencyPayIn.text = currencyCode

                        Glide.with(binding.ivFlagPayIn)
                            .load(it)
                            .into(binding.ivFlagPayIn)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when(val prg = state.fundAccountProgress) {
                        is FundAccountProgress.Default -> {}

                        is FundAccountProgress.ErrorFundingAccount -> {
                            requireActivity().showErrorAlerter(prg.message) {

                            }
                        }

                        is FundAccountProgress.FundedSuccessfully -> {
                            requireActivity().showSuccessAlerter(prg.amount, 1300) {
                                navController.navigateUp()
                            }
                        }
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