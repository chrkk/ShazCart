package com.shaz.shazcart.screens.reminder

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.Reminder

class ReminderModel(private val app: CustomApp) {

    // Mock dataset — in a full app, this might come from a local Room DB or API
    private val remindersList = mutableListOf(
        Reminder("Restock Required", "Rice (5kg) is running low and needs restocking soon.", false),
        Reminder("Pending Payment", "Juan hasn't settled his share (₱240) for 3 days.", false),
        Reminder("Sale Alert", "Cooking oil is on discount at nearby stores.", false)
    )

    fun getReminders(): List<Reminder> {
        return remindersList
    }

    fun markReminderAsRead(position: Int) {
        if (position >= 0 && position < remindersList.size) {
            val oldReminder = remindersList[position]
            remindersList[position] = oldReminder.copy(isRead = true)
        }
    }
}