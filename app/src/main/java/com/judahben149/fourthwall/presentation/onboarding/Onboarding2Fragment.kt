package com.judahben149.fourthwall.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOnboarding2Binding
import com.judahben149.fourthwall.utils.views.setup3DBounceAnimation
import com.judahben149.fourthwall.utils.views.startJiggleAnimation
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Onboarding2Fragment : Fragment() {


    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.img_calculator)
            .into(binding.icBigIcon)

        binding.icBigIcon.startJiggleAnimation()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}