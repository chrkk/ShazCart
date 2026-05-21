package com.shaz.shazcart.screens.profile

import com.shaz.shazcart.data.User

interface ProfileContract {
    data class ProfileSummary(
        val modeLabel: String,
        val headline: String,
        val primaryLabel: String,
        val primaryValue: String,
        val secondaryLabel: String,
        val secondaryValue: String,
        val note: String
    )

    interface View {
        fun displayUserInfo(user: User)
        fun displayProfileSummary(summary: ProfileSummary)
    }

    interface Presenter {
        fun loadProfileDetails()
    }
}