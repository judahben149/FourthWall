package com.judahben149.fourthwall.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.judahben149.fourthwall.databinding.FragmentDashboardBinding
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        with(binding) {
            // set data
            switchBiometricAuth.isChecked = sessionManager.isBiometricsEnabled()
            switchStoreVc.isChecked = sessionManager.isStoringVerifiableCredentialsEnabled()

            // Register listeners
            switchBiometricAuth.setOnCheckedChangeListener { buttonView, isChecked ->
                sessionManager.toggleBiometrics(isChecked)
            }

            switchStoreVc.setOnCheckedChangeListener { buttonView, isChecked ->
                sessionManager.toggleShouldStoreVerifiableCredentials(isChecked)
            }

            btnRevokeVc.setOnClickListener {
                sessionManager.revokeVCs()
                requireActivity().showSuccessAlerter("Verifiable Credentials revoked successfully", 2000) {}
            }

            btnRevokeDid.setOnClickListener {
                sessionManager.revokeDid()
                requireActivity().showSuccessAlerter("Decentralized Identifier revoked successfully", 2000) {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}