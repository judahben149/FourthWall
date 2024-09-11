package com.judahben149.fourthwall.presentation.registration

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ActivityUserRegistrationBinding
import com.judahben149.fourthwall.databinding.BottomsheetLayoutRegistrationBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.presentation.MainActivity
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.isLoading
import com.judahben149.fourthwall.utils.views.showErrorAlerter
import com.judahben149.fourthwall.utils.views.showSnack
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
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: BottomsheetLayoutRegistrationBinding

    private lateinit var country: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityUserRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomSheet()
        observeState()
        setListeners()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state.continueBtnState) {
                        is ContinueButtonState.Enabled -> {
                            binding.btnContinue.enable(resources)
                        }
                        is ContinueButtonState.Disabled -> {
                            binding.btnContinue.disable(resources)
                        }
                    }

                    when(val status = state.credProgressState) {
                        is CredentialsProgressState.InProgress ->{}
                        is CredentialsProgressState.Default ->{}
                        is CredentialsProgressState.Error ->{
                            bottomSheetDialog.dismiss()
                            showErrorAlerter(status.message) {

                            }
                        }
                        is CredentialsProgressState.Success ->{
                            bottomSheetDialog.dismiss()
                            showSuccessAlerter(status.message) {
                                navigateToHomeScreen()
                            }
                        }
                    }

                    when (state.getCredBtnState) {
                        is GetCredentialsButtonState.Enabled -> {
                            bottomSheetBinding.btnGetCredentials.enable(resources, bottomSheetBinding.progressBar)
                        }
                        is GetCredentialsButtonState.Disabled -> {
                            bottomSheetBinding.btnGetCredentials.disable(resources)
                        }
                        GetCredentialsButtonState.Loading -> {
                            bottomSheetBinding.btnGetCredentials.isLoading(resources, bottomSheetBinding.progressBar)
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

            btnContinue.setOnClickListener {
                bottomSheetDialog.show()
            }
        }

        bottomSheetBinding.run {
            cbUserAgree.setOnCheckedChangeListener { _, isChecked ->
                viewModel.toggleUserAgree(isChecked)
            }

            btnGetCredentials.setOnClickListener {
                viewModel.getCredentials()
            }
        }
    }

    private fun setupBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetBinding = BottomsheetLayoutRegistrationBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
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
        }
    }

    private fun navigateToHomeScreen() {
        sessionManager.updateHasCompletedOnboarding(true)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
    }
}