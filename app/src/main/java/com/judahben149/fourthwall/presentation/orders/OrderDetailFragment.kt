package com.judahben149.fourthwall.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOrderDetailBinding
import com.judahben149.fourthwall.domain.models.FwOrder
import com.judahben149.fourthwall.domain.models.enums.FwOrderStatus
import com.judahben149.fourthwall.utils.CurrencyUtils.getCountryFlag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private val viewModel: OrderDetailViewModel by viewModels()
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBar.setOnClickListener { navController.navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderDetail.collect { order ->
                    order?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(order: FwOrder) {
        with(binding) {
            getCountryFlag(requireContext(), order.payInCurrency)?.let { flagIcon.setImageResource(it) }

            amountAndCurrency.text = "${order.payInCurrency}${order.payInAmount}"
            status.text = order.fwOrderStatus.toString()
            status.setTextColor(getStatusColor(order.fwOrderStatus))

            orderDate.text = "Sent • ${formatDate(order.orderTime)}"

            recipientName.text = order.receiverName
            walletAddress.text = order.walletAddress
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun getStatusColor(status: FwOrderStatus): Int {
        return when (status) {
            FwOrderStatus.SUCCESSFUL -> ContextCompat.getColor(requireContext(), R.color.green_offers)
            FwOrderStatus.PENDING -> ContextCompat.getColor(requireContext(), R.color.orange_warning)
            FwOrderStatus.FAILED -> ContextCompat.getColor(requireContext(), R.color.bright_red_error)
            FwOrderStatus.IN_TRANSIT -> ContextCompat.getColor(requireContext(), R.color.shaded_base_purple)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}