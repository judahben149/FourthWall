package com.judahben149.fourthwall.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ActivityMainBinding
import com.judahben149.fourthwall.utils.biometrics.BiometricAuthListener
import com.judahben149.fourthwall.utils.biometrics.BiometricUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BiometricAuthListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private var isCurrentlyNavigatingFromBottomNav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        performBiometricsCheck()
        setupNavigation()
    }

    private fun performBiometricsCheck() {
        if (BiometricUtils.isBiometricReady(this)) {
            BiometricUtils.showBiometricPrompt(
                activity = this,
                listener = this,
                cryptoObject = null,
            )
        } else {
            //
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            if (!isCurrentlyNavigatingFromBottomNav) {
                isCurrentlyNavigatingFromBottomNav = true
                when(menuItem.itemId) {
                    R.id.navigation_home -> navigateToTopLevelDestination(R.id.homeFragment)
                    R.id.navigation_orders -> navigateToTopLevelDestination(R.id.ordersFragment)
                    R.id.navigation_dashboard -> navigateToTopLevelDestination(R.id.dashboardFragment)
                }
                isCurrentlyNavigatingFromBottomNav = false
                true
            } else {
                false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!isCurrentlyNavigatingFromBottomNav) {
                when (destination.id) {
                    R.id.homeFragment -> {
                        binding.bottomNav.selectedItemId = R.id.navigation_home
                        toggleBottomNavVisibility(true)
                    }
                    R.id.ordersFragment -> {
                        binding.bottomNav.selectedItemId = R.id.navigation_orders
                        toggleBottomNavVisibility(true)
                    }
                    R.id.dashboardFragment -> {
                        binding.bottomNav.selectedItemId = R.id.navigation_dashboard
                        toggleBottomNavVisibility(true)
                    }
                    else -> {
                        toggleBottomNavVisibility(false)
                    }
                }
            }
        }
    }

    private fun toggleBottomNavVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    private fun navigateToTopLevelDestination(destinationId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(navController.graph.startDestinationId, inclusive = false, saveState = true)
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .build()

        navController.navigate(destinationId, null, navOptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBiometricAuthenticateError(error: Int, errMsg: String) {
        performBiometricsCheck()
    }

    override fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult) {

    }
}