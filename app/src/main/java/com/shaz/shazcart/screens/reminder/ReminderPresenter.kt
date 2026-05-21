package com.shaz.shazcart.screens.reminder

class ReminderPresenter(
    private val view: ReminderContract.View,
    private val model: ReminderModel
) : ReminderContract.Presenter {

    override fun loadReminders() {
        val reminders = model.getReminders()
        view.displayReminders(reminders)
        view.updateUnreadCount(model.getUnreadCount())
        view.showEmptyState(reminders.isEmpty())
    }

    override fun markAsRead(position: Int) {
        model.markReminderAsRead(position)
        view.showMessage("Reminder marked as read.")
        loadReminders() // Refresh view
    }

    override fun deleteReminder(position: Int) {
        model.deleteReminder(position)
        view.showMessage("Reminder deleted.")
        loadReminders()
    }

    override fun markAllAsRead() {
        model.markAllAsRead()
        view.showMessage("All reminders marked as read.")
        loadReminders()
    }
}