package com.shaz.shazcart.screens.profile

import com.shaz.shazcart.data.User

interface ProfileContract {
    interface View {
        fun displayUserInfo(user: User)
        fun displayGroupContributions(contributed: Double, owed: Double)
        fun displaySoloBudget(spent: Double, budgetLimit: Double)
    }

    interface Presenter {
        fun loadProfileDetails()
    }
}