package com.shaz.shazcart.screens.reminder

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.Reminder

class ReminderModel(private val app: CustomApp) {

    fun getReminders(): List<Reminder> {
        return app.getReminders()
    }

    fun markReminderAsRead(position: Int) {
        app.markReminderAsRead(position)
    }

    fun deleteReminder(position: Int) {
        app.deleteReminder(position)
    }

    fun markAllAsRead() {
        app.markAllRemindersAsRead()
    }

    fun getUnreadCount(): Int {
        return app.getUnreadReminderCount()
    }
}