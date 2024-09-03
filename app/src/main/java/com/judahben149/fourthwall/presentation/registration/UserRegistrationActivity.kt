package com.judahben149.fourthwall.presentation.registration

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ActivityUserRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRegistrationActivity : AppCompatActivity() {

    private var _binding: ActivityUserRegistrationBinding? = null
    private val binding get() = _binding!!

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

        setListeners()
    }

    private fun setListeners() {
        binding.run {
            layoutCountry.setOnClickListener {
//                val builder: CountryPicker.Builder = Builder().with(this@UserRegistrationActivity)
//                    .listener(OnCountryPickerListener).canSearch(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}