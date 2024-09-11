package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.domain.models.FWOffering
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.generateRandomLongId
import com.judahben149.fourthwall.utils.text.extractPaymentFields
import tbdex.sdk.protocol.models.Offering
import tbdex.sdk.protocol.models.PaymentMethod
import com.judahben149.fourthwall.domain.models.PaymentMethod as FWPaymentMethod

fun Offering.toFwOffering(): FWOffering {
    return FWOffering(
        id = generateRandomLongId(),
        offeringId = this.metadata.id,
        pfiName = Constants.pfiData.getValue(this.metadata.from),
        pfiDid = this.metadata.from,
        payInCurrency = this.data.payin.currencyCode,
        payOutCurrency = this.data.payout.currencyCode,
        payoutRate = this.data.payoutUnitsPerPayinUnit,
        payInMethods = this.data.payin.methods.map { it.mapToFWPaymentMethod() },
        payOutMethods = this.data.payout.methods.map { it.mapToFWPaymentMethod() },
    )
}

fun PaymentMethod.mapToFWPaymentMethod(): FWPaymentMethod {
    val kind = this.kind
    val fields = this.requiredPaymentDetails?.let { extractPaymentFields(it) } ?: emptyList()

    return FWPaymentMethod(kind, fields)
}