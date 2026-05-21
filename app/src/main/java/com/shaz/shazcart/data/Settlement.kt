package com.shaz.shazcart.data

data class Settlement(
    val from: String,
    val to: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
