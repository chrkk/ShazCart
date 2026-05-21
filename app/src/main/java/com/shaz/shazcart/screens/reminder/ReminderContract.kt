package com.shaz.shazcart.screens.reminder

import com.shaz.shazcart.data.Reminder

interface ReminderContract {
    interface View {
        fun displayReminders(reminders: List<Reminder>)
        fun showMessage(message: String)
    }

    interface Presenter {
        fun loadReminders()
        fun markAsRead(position: Int)
    }
}