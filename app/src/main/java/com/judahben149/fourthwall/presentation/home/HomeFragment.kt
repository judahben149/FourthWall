package com.judahben149.fourthwall.presentation.home

import android.content.Intent
import android.os.Build
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
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentHomeBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.presentation.onboarding.OnboardingActivity
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
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

        observeState()
        setupBlurView()
        setClickListeners()
    }

    private fun setupBlurView() {
        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        binding.layoutBlur.findViewById<BlurView>(R.id.blurView)
            .setupWith(rootView, getBlurAlgorithm())
            .setFrameClearDrawable(windowBackground)
            .setBlurAutoUpdate(true)
    }

    private fun setClickListeners() {
        binding.btnSendMoney.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_order_flow_nav)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.value.userAccount.collect { userAccount ->
                    userAccount?.let { it ->
                        val currencyAccounts = it.currencyAccountEntities.sortedByDescending { it.isPrimaryAccount }

                        binding.run {
                            currencyAccounts[0].let {
                                tvAmountBalance.text = it.balance.formatCurrency(it.currencyCode)

                            }
                        }
                    }
                }
            }
        }
    }

private fun getBlurAlgorithm(): BlurAlgorithm {

    val algorithm: BlurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        RenderEffectBlur()
    } else {
        RenderScriptBlur(requireContext())
    }

    return algorithm
}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}