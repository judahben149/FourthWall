package com.judahben149.fourthwall.presentation.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ActivityUserRegistrationBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.presentation.MainActivity
import com.judahben149.fourthwall.presentation.onboarding.FinalOnboardingActivity
import com.judahben149.fourthwall.presentation.onboarding.OnboardingActivity
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.OnCountryPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserRegistrationActivity : AppCompatActivity(), OnCountryPickerListener {

    private var _binding: ActivityUserRegistrationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: UserRegistrationViewModel by viewModels()

    private lateinit var country: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.hasCompletedOnboarding()) {
            if (sessionManager.hasStartedButNotCompletedOnboarding()) {

                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        enableEdgeToEdge()

        _binding = ActivityUserRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initializeUiState()
        observeState()
        setListeners()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (val status = state.userLoginProgress) {
                        is UserLoginProgress.HasNotSignedUp -> {
                            binding.run {
                                listOf(
                                    layoutName,
                                    layoutCountry,
                                    layoutEmail,
                                    layoutPassword,
                                    toolbarTitleSignUp
                                ).forEach {
                                    it.visibility = View.VISIBLE
                                }

                                toolbarTitleSignIn.visibility = View.GONE
                                btnSignIn.visibility = View.GONE
                                btnSignUp.visibility = View.VISIBLE

                                btnSignUp.disable(resources, binding.progressBarSignUp)
                            }
                        }

                        is UserLoginProgress.HasSignedUpButIsNotSignedIn -> {
                            binding.run {
                                listOf(layoutName, layoutCountry, toolbarTitleSignUp).forEach {
                                    it.visibility = View.GONE
                                }

                                toolbarTitleSignIn.visibility = View.VISIBLE
                                btnSignIn.visibility = View.VISIBLE
                                btnSignUp.visibility = View.GONE

                                btnSignIn.disable(resources, binding.progressBarSignIn)
                            }
                        }

                        is UserLoginProgress.ErrorSigningUp -> {
                            showErrorAlerter(status.message) {}
                            binding.btnSignUp.enable(resources, binding.progressBarSignUp)
                        }

                        is UserLoginProgress.SuccessSigningUp -> {
                            showSuccessAlerter(status.message) {
                                navigateToFinalOnboardingScreen()
                            }
                        }

                        is UserLoginProgress.ErrorSigningIn -> {
                            showErrorAlerter(status.message) {}
                            binding.btnSignIn.enable(resources, binding.progressBarSignIn)
                        }

                        is UserLoginProgress.IsReadyToSignIn -> {

                        }

                        is UserLoginProgress.IsReadyToSignUp -> {

                        }

                        is UserLoginProgress.SuccessSigningIn -> {
                            showSuccessAlerter(status.message) {
                                navigateToMainScreen()
                            }
                        }

                        is UserLoginProgress.RunningOperation -> {
                            binding.btnSignUp.isLoading(resources, binding.progressBarSignUp)
                            binding.btnSignIn.isLoading(resources, binding.progressBarSignIn)
                        }
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.run {
            toolBar.setNavigationOnClickListener {
                finish()
            }

            tvName.doAfterTextChanged {
                viewModel.updateName(it.toString())
                evaluateButtonStatus()
            }

            tvEmail.doAfterTextChanged {
                viewModel.updateEmail(it.toString())
                evaluateButtonStatus()
            }

            tvPassword.doAfterTextChanged {
                viewModel.updatePassword(it.toString())
                evaluateButtonStatus()
            }

            layoutCountry.setOnClickListener {
                tvName.clearFocus()
                it.requestFocus()

                val builder = CountryPicker
                    .Builder()
                    .with(this@UserRegistrationActivity)
                    .listener(this@UserRegistrationActivity)
                    .style(R.style.CountryPickerStyle)
                    .canSearch(true)

                val picker = builder.build()
                picker.showBottomSheet(this@UserRegistrationActivity)
            }

            btnSignUp.setOnClickListener {
                viewModel.signUp()
            }

            btnSignIn.setOnClickListener {
                viewModel.signIn()
            }
        }
    }

    override fun onSelectCountry(country: Country?) {
        country?.let {
            binding.run {
                tvCountry.text = it.name.plus(" (${it.code})")
                ivFlag.setImageResource(it.flag)
                ivFlag.visibility = View.VISIBLE
            }

            this.country = it
            viewModel.updateCountryCode(country.code)
            viewModel.updateCurrencyCode(country.currency)
            evaluateButtonStatus()
        }
    }

    private fun evaluateButtonStatus() {
        when (viewModel.state.value.userLoginProgress) {
            UserLoginProgress.HasSignedUpButIsNotSignedIn -> {
                binding.run {
                    if (
                        tvEmail.text.isNullOrEmpty().not() && tvPassword.text.isNullOrEmpty().not()
                    ) {
                        btnSignIn.enable(resources, progressBarSignIn)
                    } else {
                        btnSignIn.disable(resources, progressBarSignIn)
                    }
                }
            }

            UserLoginProgress.HasNotSignedUp -> {
                binding.run {
                    if (
                        tvEmail.text.isNullOrEmpty().not() &&
                        tvPassword.text.isNullOrEmpty().not() &&
                        tvName.text.isNullOrEmpty().not() &&
                        viewModel.state.value.countryCode.isNotEmpty()
                    ) {
                        btnSignUp.enable(resources, progressBarSignUp)
                    } else {
                        btnSignUp.disable(resources, progressBarSignUp)
                    }
                }

            }

            else -> {

            }
        }
    }

    private fun navigateToMainScreen() {
        sessionManager.updateHasCompletedOnboarding(true)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToFinalOnboardingScreen() {
        sessionManager.updateHasCompletedOnboarding(true)

        val intent = Intent(this, FinalOnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initializeUiState() {
        viewModel.getSignUpState()
    }

    override fun onResume() {
        super.onResume()

//        val country = CountryPicker.Builder()
//        binding.tvName.setText(viewModel.state.value.name)
//        binding.tvCountry.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        sessionManager.updateHasStartedButNotCompletedOnboarding(true)
    }
}