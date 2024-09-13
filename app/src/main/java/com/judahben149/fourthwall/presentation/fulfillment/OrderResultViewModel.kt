package com.judahben149.fourthwall.presentation.fulfillment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.models.PfiRating
import com.judahben149.fourthwall.domain.usecase.pfiRating.InsertPfiRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderResultViewModel @Inject constructor(
    private val insertPfiRatingUseCase: InsertPfiRatingUseCase,
): ViewModel() {


    fun insertRating(pfiRating: PfiRating) {
        viewModelScope.launch {
            insertPfiRatingUseCase(pfiRating)
        }
    }
}