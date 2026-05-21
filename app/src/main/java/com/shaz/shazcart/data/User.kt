package com.shaz.shazcart.data

data class User(
    var displayName: String = "",
    var email: String = "",
    var password: String = "",
    var mode: String = "Group",
    var budgetLimit: Double = 0.0
)