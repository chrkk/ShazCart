package com.shaz.shazcart.screens.welcome

class WelcomeModel : WelcomeContract.Model {
    override fun getWelcomeMessage(): String {
        return "Welcome to ShazCart!"
    }
}