package com.shaz.shazcart.screens.register

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User

class RegisterModel(private val app: CustomApp) : RegisterContract.Model {
    override fun registerUser(name: String, email: String, password: String): Boolean {
        return if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            app.setUser(User(email, password))   // store new user
            app.setLoggedIn(false)               // not logged in yet
            true
        } else {
            false
        }
    }
}
