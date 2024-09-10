package com.judahben149.fourthwall.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentFundWalletBinding
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
            toolBar.setOnClickListener { navController.popBackStack(
                destinationId = R.id.order_flow_nav,
                inclusive = true,
                saveState = false
            ) }

            btnFund.setOnClickListener {
                if ()
            }
        }

        viewModel.getCurrencyCode()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}