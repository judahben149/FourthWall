package com.judahben149.fourthwall.domain.models

data class PaymentMethod(
    val kind: String,
    val paymentFields: List<PaymentField>
)

data class PaymentField(
    val fieldName: String,
    val required: Boolean
)