package com.judahben149.fourthwall.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.judahben149.fourthwall.databinding.ActivityFinalOnboardingBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.presentation.MainActivity
import com.judahben149.fourthwall.presentation.login.UserRegistrationViewModel
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.startTakeoffAnimation
import com.judahben149.fourthwall.utils.views.startWarmupAnimation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FinalOnboardingActivity : AppCompatActivity() {


    private var _binding: ActivityFinalOnboardingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: UserRegistrationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityFinalOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.run {
            btnGetStarted.disable(resources)
            icBigIcon.startWarmupAnimation()

            cbTermsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    btnGetStarted.enable(resources)
                } else {
                    btnGetStarted.disable(resources)
                }
            }

            btnGetStarted.setOnClickListener {
                icBigIcon.clearAnimation()
                icBigIcon.startTakeoffAnimation {
                    sessionManager.updateHasStartedButNotCompletedOnboarding(false)
                    sessionManager.updateHasCompletedOnboarding(true)
                    navigateToHomeScreen()
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}