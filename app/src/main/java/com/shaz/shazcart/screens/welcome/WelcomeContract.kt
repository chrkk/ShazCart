package com.shaz.shazcart.screens.welcome

interface WelcomeContract {
    interface View {
        fun showGetStarted()
        fun showLogin()
    }

    interface Presenter {
        fun onGetStartedClicked()
        fun onLoginClicked()
    }

    interface Model {
        fun getWelcomeMessage(): String
    }
}