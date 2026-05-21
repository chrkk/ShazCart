package com.shaz.shazcart.data

data class Reminder(
    val title: String,
    val description: String,
    val isRead: Boolean = false
)