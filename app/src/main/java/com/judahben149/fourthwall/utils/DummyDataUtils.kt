package com.judahben149.fourthwall.utils

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import kotlin.random.Random


object DummyDataUtils {

    private val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD", "NZD")
    private val pfiNames = listOf("Bank A", "Bank B", "Credit Union C", "Financial Inst D", "E-Wallet F")
    private val receiverNames = listOf("John Doe", "Jane Smith", "Alice Johnson", "Bob Wilson", "Carol Brown")

    fun generateDummyOrders(count: Int): List<OrderEntity> {
        return (1..count).map { generateDummyOrder(it) }
    }

    private fun generateDummyOrder(id: Int): OrderEntity {
        val payInAmount = Random.nextDouble(1.0, 1000000.0)
        val exchangeRate = Random.nextDouble(0.8, 1.2)
        return OrderEntity(
            orderId = id,
            orderExchangeId = id.toString(),
            payInAmount = payInAmount,
            payOutAmount = payInAmount * exchangeRate,
            payInCurrency = currencies.random(),
            payOutCurrency = currencies.random(),
            payOutMethod = Random.nextInt(3),
            orderTime = System.currentTimeMillis() - Random.nextLong(30L * 24 * 60 * 60 * 1000),
            orderType = Random.nextInt(2),
            orderStatus = Random.nextInt(3),
            pfiName = pfiNames.random(),
            pfiDid = pfiNames.random(),
            recipientAccount = receiverNames.random(),
            walletAddress = generateRandomWalletAddress(),
            payoutFee = 2.3
        )
    }

    private fun generateRandomWalletAddress(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..34).map { chars.random() }.joinToString("")
    }
}