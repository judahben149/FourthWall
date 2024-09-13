package com.judahben149.fourthwall.presentation.fulfillment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.FragmentOrderResultBinding
import com.judahben149.fourthwall.domain.models.FwOrderResult
import com.judahben149.fourthwall.domain.models.PfiRating
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import com.judahben149.fourthwall.utils.views.animateCheckmark
import com.judahben149.fourthwall.utils.views.showSuccessAlerter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderResultFragment : Fragment(), RatingBar.OnRatingBarChangeListener {

    private var _binding: FragmentOrderResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderResult: FwOrderResult

    private val navController by lazy { findNavController() }
    private val viewModel: OrderResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderResultJson = arguments?.getString("orderResult")
        orderResult = Gson().fromJson(orderResultJson, FwOrderResult::class.java)


        val successMessage = getString(
            R.string.transaction_success_message,
            orderResult.payInAmount.toDouble().formatCurrency(orderResult.payInCurrency)
        )

        val pfiName = Constants.pfiData.getValue(orderResult.pfiDid)

        binding.run {
            tvResultLabel.text = successMessage
            btnGoHome.setOnClickListener { navController.popBackStack() }

            tvRatingLabel.text = getString(R.string.rate_your_experience_with, pfiName)
            ratingBar.onRatingBarChangeListener = this@OrderResultFragment
            icBigIcon.animateCheckmark()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (fromUser) {
            viewModel.insertRating(
                PfiRating(
                    ratingId = 0,
                    pfiDid = orderResult.pfiDid,
                    pfiRating = rating.toDouble()
                )
            )

            requireActivity().showSuccessAlerter("Rating successful", 1400) {}
        }
    }
}