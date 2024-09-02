package com.judahben149.fourthwall.utils

import com.judahben149.fourthwall.data.local.entities.Order
import kotlin.math.abs
import kotlin.random.Random

object DummyDataUtils {

    private val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD", "NZD")
    private val pfiNames = listOf("Bank A", "Bank B", "Credit Union C", "Financial Inst D", "E-Wallet F")

    fun generateDummyOrders(count: Int): List<Order> {
        return (1..count).map { generateDummyOrder(it) }
    }

    private fun generateDummyOrder(id: Int): Order {
        return Order(
            orderId = id,
            amount = abs(Random.nextLong()) % 1000000 + 1, // 1 to 1,000,000
            payInCurrency = currencies.random(),
            payOutCurrency = currencies.random(),
            payOutMethod = Random.nextInt(5), // Assuming 5 different payout methods
            orderTime = System.currentTimeMillis() - Random.nextLong() % (30L * 24 * 60 * 60 * 1000), // Within last 30 days
            orderStatus = Random.nextInt(5), // Assuming 5 different order statuses
            pfiName = pfiNames.random()
        )
    }
}