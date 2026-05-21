package com.shaz.shazcart.data

data class User(
    var username: String = "",
    var password: String = "",
    var mode: String = "Group" // "Group" or "Solo"
)