package com.shaz.shazcart.screens.reminder

class ReminderPresenter(
    private val view: ReminderContract.View,
    private val model: ReminderModel
) : ReminderContract.Presenter {

    override fun loadReminders() {
        val reminders = model.getReminders()
        view.displayReminders(reminders)
    }

    override fun markAsRead(position: Int) {
        model.markReminderAsRead(position)
        view.showMessage("Reminder marked as read.")
        loadReminders() // Refresh view
    }
}