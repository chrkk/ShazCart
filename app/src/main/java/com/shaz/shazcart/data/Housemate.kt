package com.shaz.shazcart.data

data class Housemate(
    var name: String = "",
    var amountOwed: Double = 0.0,
    var status: String = "",
    var settlementPaid: Double = 0.0,
    var settlementReceived: Double = 0.0,
    var netBalance: Double = 0.0
)