package com.judahben149.fourthwall.presentation.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {


    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    val navController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.viewPager
        val adapter = OnboardingPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        setupPageChangeCallback()

        binding.run {
            btnNext.setOnClickListener {
                if (viewPager.currentItem < 2) {
                    viewPager.currentItem += 1
                } else {
                    navController.navigate(R.id.action_onboardingFragment_to_homeFragment)
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
            2 -> binding.btnNext.text = "Get Started"
            else -> binding.btnNext.text = "Next"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}