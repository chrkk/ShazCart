package com.shaz.shazcart.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.shaz.shazcart.data.User

class CustomApp : Application() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Log.e("Custom app", "onCreate is called")
        prefs = getSharedPreferences("shazcart_prefs", Context.MODE_PRIVATE)
    }

    fun getUser(): User {
        val username = prefs.getString("usernams", "user") ?: "user"
        val password = prefs.getString("password", "1234") ?: "1234"
        return User(username, password)
    }

    fun setUser(newUser: User) {
        prefs.edit()
            .putString("username", newUser.username)
            .putString("password", newUser.password)
            .apply()
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun clearUser() {
        prefs.edit()
            .remove("username")
            .remove("password")
            .putBoolean("is_logged_in", false)
            .apply()
    }
}