package com.judahben149.fourthwall.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ActivityOnboardingBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.presentation.login.UserRegistrationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {


    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager()
    }

    private fun setupViewPager() {

        val viewPager: ViewPager2 = binding.viewPager
        val adapter = OnboardingPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        setupPageChangeCallback()

        binding.run {
            btnNext.setOnClickListener {
                if (viewPager.currentItem < 2) {
                    viewPager.currentItem += 1
                } else {
                    navigateToLoginScreen()
                }
            }
        }
    }

    private fun setupPageChangeCallback() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonText(position)
            }
        })
    }

    private fun updateButtonText(position: Int) {
        when (position) {
            2 -> binding.btnNext.text = getString(R.string.get_started)
            else -> binding.btnNext.text = getString(R.string.next)
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, UserRegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        sessionManager.updateHasStartedButNotCompletedOnboarding(true)
        _binding = null
    }
}