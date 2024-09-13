package com.judahben149.fourthwall.presentation.home

import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentHomeBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.mappers.toCurrencyAccount
import com.judahben149.fourthwall.domain.models.CurrencyAccount
import com.judahben149.fourthwall.presentation.onboarding.OnboardingActivity
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import com.judahben149.fourthwall.utils.log
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!sessionManager.hasCompletedOnboarding()) {
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
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

        setupUserAccountRecyclerView()
        observeState()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.run {
            ivProfile.setOnClickListener { navController.navigate(R.id.dashboardFragment) }

            btnAddAccount.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_createCurrencyAccountFragment)
            }
            btnSendMoney.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_order_flow_nav)
            }
        }
    }

    private fun setupUserAccountRecyclerView() {
        binding.layoutCardNoAccount.visibility = View.GONE

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
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.value.userAccount.collect { userAccount ->

                    if (userAccount != null) {
                        val currencyAccounts = userAccount.currencyAccountEntities
                            .map { entity -> entity.toCurrencyAccount() }
                            .sortedByDescending { account -> account.isPrimaryAccount }

                        userAccount.log("USER ACCOUNTS ---> ")
                        currencyAccounts.log("CURRENCY ACCOUNTS ---> ")
                        adapter.updateItems(currencyAccounts)
                    } else {
                        binding.layoutCardNoAccount.visibility = View.VISIBLE
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