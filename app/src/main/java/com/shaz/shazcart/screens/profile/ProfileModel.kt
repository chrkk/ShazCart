package com.shaz.shazcart.screens.profile

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User
import java.util.Locale

class ProfileModel(private val app: CustomApp) {
    fun getUser(): User {
        return app.getUser()
    }

    private fun formatMoney(value: Double): String {
        return String.format(Locale.getDefault(), "₱%.2f", value)
    }

    fun getProfileSummary(): ProfileContract.ProfileSummary {
        val user = getUser()
        val savedBudget = user.budgetLimit
        val budgetText = if (savedBudget > 0.0) formatMoney(savedBudget) else "No budget saved"

        return if (user.mode == "Solo") {
            ProfileContract.ProfileSummary(
                modeLabel = "Solo mode",
                headline = "Your personal budget hub",
                primaryLabel = "Saved budget",
                primaryValue = budgetText,
                secondaryLabel = "What this means",
                secondaryValue = if (savedBudget > 0.0) {
                    "Use this as your spending limit in the dashboard."
                } else {
                    "Set a budget in the dashboard to enable spending alerts."
                },
                note = if (savedBudget > 0.0) {
                    "You are viewing your real saved budget, not a hardcoded demo value."
                } else {
                    "Save a budget first so the app can help you stay on track."
                }
            )
        } else {
            ProfileContract.ProfileSummary(
                modeLabel = "Group mode",
                headline = "Shared house profile",
                primaryLabel = "Saved budget",
                primaryValue = budgetText,
                secondaryLabel = "How to use it",
                secondaryValue = if (savedBudget > 0.0) {
                    "This budget is informational here; group splits are managed from the dashboard."
                } else {
                    "Group mode focuses on shared balances instead of a fixed personal budget."
                },
                note = "Use the dashboard for housemates, groceries, and settlement tracking."
            )
        }
    }
}