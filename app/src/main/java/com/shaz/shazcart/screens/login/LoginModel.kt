package com.shaz.shazcart.screens.login

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User

class LoginModel(private val app: CustomApp) {
    fun isValidCredentials(username: String, password: String): Boolean {
        val user: User = app.getUser()
        return (user.username.equals(username, false)
                && user.password.equals(password, false))
    }

    fun setLoggedIn(loggedIn: Boolean) {
        app.setLoggedIn(loggedIn)
    }
}

