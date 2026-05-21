package com.shaz.shazcart.data

data class User(
    var username: String = "",
    var password: String = "",
    var mode: String = "Group",
    var budgetLimit: Double = 0.0 // Add this field
)