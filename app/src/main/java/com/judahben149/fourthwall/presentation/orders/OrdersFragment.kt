package com.judahben149.fourthwall.presentation.orders

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.judahben149.fourthwall.databinding.FragmentOrdersBinding
import com.judahben149.fourthwall.domain.mappers.toFwOrder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private val navController by lazy { findNavController() }
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.value.allOrders.collect { it ->

                    if (it.isEmpty()) {
                        binding.tvEmptyLabel.visibility = View.VISIBLE
                        binding.animEmpty.visibility = View.VISIBLE
                        binding.rvOrders.visibility = View.GONE
                    } else {
                        binding.tvEmptyLabel.visibility = View.GONE
                        binding.animEmpty.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                    }

                    orderAdapter.submitOrders(it.map { it.toFwOrder() })
                }
            }
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(requireContext()) { orderId ->
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(orderId)
            navController.navigate(action)
        }

        binding.rvOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}