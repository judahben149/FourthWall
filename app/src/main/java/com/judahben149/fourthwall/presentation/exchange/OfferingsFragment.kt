package com.judahben149.fourthwall.presentation.exchange

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
import com.judahben149.fourthwall.databinding.FragmentOfferingsBinding
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable
import com.judahben149.fourthwall.utils.views.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OfferingsFragment : Fragment() {

    private var _binding: FragmentOfferingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OfferingsViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offeringsState.collect { state ->

                    when(state) {
                        is OfferingsState.Loading -> {
                            binding.btnContinue.disable(resources, binding.progressBar)
                        }

                        is OfferingsState.Error -> {
                            binding.btnContinue.disable(resources, binding.progressBar)
                            showSnack(state.message, binding.root)
                        }

                        is OfferingsState.Success -> {
                            Log.d("TAGTAG", state.offerings.toString())
                            binding.btnContinue.enable(resources, binding.progressBar)
                        }
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