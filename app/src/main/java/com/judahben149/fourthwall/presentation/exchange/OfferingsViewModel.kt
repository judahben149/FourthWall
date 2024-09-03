package com.judahben149.fourthwall.presentation.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.usecase.pfi.GetPfiDataUseCase
import com.judahben149.fourthwall.domain.usecase.pfi.GetPfiOfferingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tbdex.sdk.protocol.models.Offering
import javax.inject.Inject

@HiltViewModel
class OfferingsViewModel @Inject constructor(
    private val getPfiDataUseCase: GetPfiDataUseCase,
    private val getPfiOfferingsUseCase: GetPfiOfferingsUseCase
) : ViewModel() {

    private val _offeringsState = MutableStateFlow<OfferingsState>(OfferingsState.Loading)
    val offeringsState: StateFlow<OfferingsState> = _offeringsState

    init {
        getPfiOfferings()
    }

    private fun getPfiOfferings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _offeringsState.update { OfferingsState.Loading }

                val pfiData = getPfiDataUseCase()

                val offeringsList = pfiData.map { pfi ->
                    async {
                        getPfiOfferingsUseCase(pfi)
                    }
                }.awaitAll()

                _offeringsState.update { OfferingsState.Success(offeringsList.flatten()) }

            } catch (ex: Exception) {
                _offeringsState.update { OfferingsState.Error(ex.message ?: "An error occurred") }
            }
        }
    }


}


sealed class OfferingsState {
    data object Loading : OfferingsState()
    data class Success(val offerings: List<Offering>) : OfferingsState()
    data class Error(val message: String) : OfferingsState()
}