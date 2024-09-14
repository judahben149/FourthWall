package com.judahben149.fourthwall.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentHomeBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.mappers.toCurrencyAccount
import com.judahben149.fourthwall.domain.mappers.toOrder
import com.judahben149.fourthwall.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager
    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: UserAccountAdapter
    private lateinit var recentOrdersAdapter: RecentOrdersAdapter


    private val handleSendMoneyClick: (Int) -> Unit = {
        navController.navigate(R.id.action_homeFragment_to_order_flow_nav)
    }

    private val handleAddAccountClick: (Int) -> Unit = {
        navController.navigate(R.id.action_homeFragment_to_createCurrencyAccountFragment)
    }

    private val handleFundAccountClick: (Int) -> Unit = { itemId ->
        Constants.currencyAccountId = itemId
        navController.navigate(R.id.action_homeFragment_to_fundWalletFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        setClickListeners()
        setupUserAccountRecyclerView()
        setupOrdersRecyclerView()
    }

    private fun setClickListeners() {
        binding.run {
            ivProfile.setOnClickListener {
                navController.navigate(
                    R.id.dashboardFragment,
                    null,
                    getNavOptionsForTopLevelDestinations(R.id.homeFragment)
                )
            }

            btnSeeAllOrders.setOnClickListener {
                navController.navigate(
                    R.id.ordersFragment,
                    null,
                    getNavOptionsForTopLevelDestinations(R.id.homeFragment)
                )
            }
        }
    }

    private fun setupUserAccountRecyclerView() {

        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        adapter = UserAccountAdapter(
            context = requireContext(),
            rootView = rootView,
            windowBackground = windowBackground,
            onSendMoneyClick = handleSendMoneyClick,
            onAddAccountClick = handleAddAccountClick,
            onFundAccountClick = handleFundAccountClick
        )

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvUserAccount.adapter = adapter
        binding.rvUserAccount.layoutManager = layoutManager


        val snapHelper = PagerSnapHelper()

        if (binding.rvUserAccount.onFlingListener == null) {
            snapHelper.attachToRecyclerView(binding.rvUserAccount)
        }

        binding.rvUserAccount.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lm = recyclerView.layoutManager as LinearLayoutManager
                val position = lm.findFirstVisibleItemPosition()

                if (position != RecyclerView.NO_POSITION) {
                    setCurrentIndicator(position)
                }
            }
        })
    }

    private fun setupOrdersRecyclerView() {
        recentOrdersAdapter = RecentOrdersAdapter(requireContext())

        binding.rvLatestOrders.apply {
            adapter = recentOrdersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userAccount.collect { userAccount ->
                        userAccount?.let { it ->
                            val currencyAccounts = it.currencyAccountEntities
                                .map { it.toCurrencyAccount() }
                                .sortedByDescending { it.isPrimaryAccount }

                            adapter.updateItems(currencyAccounts)
                            setupIndicators(currencyAccounts.size)
                            setCurrentIndicator(0)
                        }
                    }
                }

                launch {
                    viewModel.allOrders.collect { orderEntities ->
                        val orders = orderEntities.map { it.toOrder() }
                        recentOrdersAdapter.submitOrders(orders)
                    }
                }
            }
        }
    }

    private fun getNavOptionsForTopLevelDestinations(destinationToPop: Int): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(destinationToPop, true)
            .build()
    }

    // Simulating recycler view item decoration
    private fun setupIndicators(count: Int) {
        binding.indicatorContainer.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(count)

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {

            indicators[i] = ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
                layoutParams = params
            }

            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.indicatorContainer.childCount

        for (i in 0 until childCount) {
            val imageView = binding.indicatorContainer.getChildAt(i) as ImageView ?: continue

            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}