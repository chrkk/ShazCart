package com.shaz.shazcart.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.shaz.shazcart.data.Reminder
import com.shaz.shazcart.data.User
import org.json.JSONArray
import org.json.JSONObject

class CustomApp : Application() {
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "shazcart_prefs"
        private const val KEY_REMINDERS = "reminders"
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("Custom app", "onCreate is called")
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getUser(): User {
        val displayName = prefs.getString("displayName", "User") ?: "User"
        val email = prefs.getString("email", "") ?: ""
        val password = prefs.getString("password", "1234") ?: "1234"
        val mode = prefs.getString("mode", "Group") ?: "Group"
        val budgetLimit = prefs.getFloat("budgetLimit", 0.0f).toDouble()
        return User(displayName, email, password, mode, budgetLimit)
    }

    fun setUser(newUser: User) {
        prefs.edit()
            .putString("displayName", newUser.displayName)
            .putString("email", newUser.email)
            .putString("password", newUser.password)
            .putString("mode", newUser.mode)
            .putFloat("budgetLimit", newUser.budgetLimit.toFloat())
            .apply()
    }

    fun updateUserMode(mode: String) {
        val user = getUser()
        user.mode = mode
        setUser(user)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun clearUser() {
        prefs.edit()
            .remove("displayName")
            .remove("email")
            .remove("password")
            .remove("mode")
            .remove("budgetLimit")
            .putBoolean("is_logged_in", false)
            .apply()
    }

    fun getReminders(): MutableList<Reminder> {
        val stored = prefs.getString(KEY_REMINDERS, null)
        if (stored.isNullOrBlank()) {
            val defaults = mutableListOf(
                Reminder("Restock Required", "Rice (5kg) is running low and needs restocking soon.", false),
                Reminder("Pending Payment", "Juan hasn't settled his share (₱240) for 3 days.", false),
                Reminder("Sale Alert", "Cooking oil is on discount at nearby stores.", false)
            )
            saveReminders(defaults)
            return defaults
        }

        val items = mutableListOf<Reminder>()
        val array = JSONArray(stored)
        for (index in 0 until array.length()) {
            val objectValue = array.getJSONObject(index)
            items.add(
                Reminder(
                    title = objectValue.optString("title", ""),
                    description = objectValue.optString("description", ""),
                    isRead = objectValue.optBoolean("isRead", false)
                )
            )
        }
        return items
    }

    fun saveReminders(reminders: List<Reminder>) {
        val array = JSONArray()
        reminders.forEach { reminder ->
            array.put(
                JSONObject()
                    .put("title", reminder.title)
                    .put("description", reminder.description)
                    .put("isRead", reminder.isRead)
            )
        }
        prefs.edit().putString(KEY_REMINDERS, array.toString()).apply()
    }

    fun getUnreadReminderCount(): Int {
        return getReminders().count { !it.isRead }
    }

    fun markReminderAsRead(position: Int) {
        val reminders = getReminders()
        if (position in reminders.indices) {
            reminders[position] = reminders[position].copy(isRead = true)
            saveReminders(reminders)
        }
    }

    fun deleteReminder(position: Int) {
        val reminders = getReminders()
        if (position in reminders.indices) {
            reminders.removeAt(position)
            saveReminders(reminders)
        }
    }

    fun markAllRemindersAsRead() {
        val reminders = getReminders().map { it.copy(isRead = true) }
        saveReminders(reminders)
    }
}