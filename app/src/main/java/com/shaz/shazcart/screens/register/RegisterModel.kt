package com.shaz.shazcart.screens.register

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User

class RegisterModel(private val app: CustomApp) : RegisterContract.Model {
    override fun registerUser(name: String, email: String, password: String, mode: String): Boolean {
        return if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            app.setUser(User(email, password, mode)) // Store user with Mode
            app.setLoggedIn(false)
            true
        } else {
            false
        }
    }
}