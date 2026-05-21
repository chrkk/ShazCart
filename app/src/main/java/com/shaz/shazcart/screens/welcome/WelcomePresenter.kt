package com.shaz.shazcart.screens.welcome

class WelcomePresenter(
    private val view: WelcomeContract.View,
    private val model: WelcomeContract.Model
) : WelcomeContract.Presenter {

    override fun onGetStartedClicked() {
        // For now, just trigger view action
        view.showGetStarted()
    }

    override fun onLoginClicked() {
        view.showLogin()
    }
}