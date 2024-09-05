package com.judahben149.fourthwall.presentation.rfq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentRequestQuoteBinding
import com.judahben149.fourthwall.presentation.exchange.OfferingsViewModel
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.views.showSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestQuoteFragment : Fragment() {

    private var _binding: FragmentRequestQuoteBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: OfferingsViewModel by hiltNavGraphViewModels(R.id.order_flow_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        createRfq()
    }

    private fun setListeners() {
        binding.run {
            toolBar.setOnClickListener { navController.navigateUp() }
        }
    }

    private fun createRfq() {
        viewModel.state.value.offeringsList.let {
            it.forEach { off ->
                off.data.payout.methods.forEach { meth ->
                    ("Pay out Method (${off.data.payin.currencyCode} -> ${off.data.payout.currencyCode})- " + meth.kind).log()
                }
            }

            it.forEach { off ->
                off.data.payin.methods.forEach { meth ->
                    ("Pay in Method (${off.data.payin.currencyCode} -> ${off.data.payout.currencyCode})- " + meth.kind).log()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}