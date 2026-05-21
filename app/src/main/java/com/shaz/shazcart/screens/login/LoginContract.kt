package com.shaz.shazcart.screens.login

// BUG FIX: was declared as 'class' — must be 'interface' to match
// DashboardContract, RegisterContract, and WelcomeContract which all correctly use 'interface'
interface LoginContract {
    interface View {
        fun showEmptyMessage()
        fun showSuccessMessage()
        fun showDashboardScreen()
        fun showInvalidCredentials()
    }

    interface Presenter {
        fun validateCredentials(username: String, password: String)
    }
}
