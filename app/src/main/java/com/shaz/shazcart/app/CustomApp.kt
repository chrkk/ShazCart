package com.shaz.shazcart.app

import android.app.Application
import android.util.Log
import com.shaz.shazcart.data.User

class CustomApp : Application() {
    private var user = User("user", "1234")
    private var isLoggedIn: Boolean = false

    override fun onCreate() {
        super.onCreate()
        Log.e("Custom app", "onCreate is called")
    }

    fun getUser() = this.user

    fun setLoggedIn(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
    }

    fun isUserLoggedIn(): Boolean {
        return isLoggedIn
    }

    fun clearUser() {
        user = User("", "")
        // BUG FIX: isLoggedIn was never reset to false here.
        // Without this, WelcomeActivity.isUserLoggedIn() returns true even after logout,
        // causing the app to immediately redirect back to DashboardActivity on relaunch.
        isLoggedIn = false
    }

    fun setUser(newUser: User) {
        user = newUser
    }
}
