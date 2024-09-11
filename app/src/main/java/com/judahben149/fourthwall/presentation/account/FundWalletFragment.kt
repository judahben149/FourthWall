package com.judahben149.fourthwall.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.databinding.FragmentFundWalletBinding
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.CurrencyUtils.getCountryFlag
import com.judahben149.fourthwall.utils.views.showInfoAlerter
import dagger.hilt.android.AndroidEntryPoint

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

            btnFund.setOnClickListener {
                try {
                    val amount = binding.etFundAccount.text.toString().toDouble()
                    viewModel.fundAccount(Constants.currencyAccountId, amount)

                } catch (ex: Exception) {
                    requireActivity().showInfoAlerter("Error funding account. Please retry")
                }
            }
        }

        viewModel.getCurrencyCode(Constants.currencyAccountId) { currencyCode ->
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}