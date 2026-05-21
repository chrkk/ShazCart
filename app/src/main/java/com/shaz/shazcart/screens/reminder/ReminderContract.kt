package com.shaz.shazcart.screens.reminder

import com.shaz.shazcart.data.Reminder

interface ReminderContract {
    interface View {
        fun displayReminders(reminders: List<Reminder>)
        fun showMessage(message: String)
        fun updateUnreadCount(count: Int)
        fun showEmptyState(isEmpty: Boolean)
    }

    interface Presenter {
        fun loadReminders()
        fun markAsRead(position: Int)
        fun deleteReminder(position: Int)
        fun markAllAsRead()
    }
}