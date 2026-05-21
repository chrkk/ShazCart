package com.shaz.shazcart.screens.profile

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User

class ProfileModel(private val app: CustomApp) {
    fun getUser(): User {
        return app.getUser()
    }

    // Mock data for group context
    fun getGroupStats(): Pair<Double, Double> {
        return Pair(850.0, 120.0) // Contributed, Owed
    }

    // Mock data for solo context
    fun getSoloStats(): Pair<Double, Double> {
        return Pair(1800.0, 3000.0) // Spent, Budget Limit
    }
}