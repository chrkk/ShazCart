package com.shaz.shazcart.screens.reminder

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.Reminder
import com.shaz.shazcart.helper.DashboardRecyclerAdapter

class ReminderActivity : AppCompatActivity(), ReminderContract.View {

    private lateinit var presenter: ReminderPresenter
    private lateinit var reminderAdapter: DashboardRecyclerAdapter<Reminder>
    private lateinit var textviewUnreadCount: TextView
    private lateinit var textviewEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        presenter = ReminderPresenter(this, ReminderModel(application as CustomApp))

        textviewUnreadCount = findViewById(R.id.textviewUnreadCount)
        textviewEmptyState = findViewById(R.id.textviewEmptyState)

        setupRecyclerView()

        findViewById<Button>(R.id.buttonMarkAllRead).setOnClickListener {
            presenter.markAllAsRead()
        }

        findViewById<Button>(R.id.buttonBackToDashboard).setOnClickListener {
            finish()
        }

        presenter.loadReminders()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerviewReminders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Reusing the robust Recycler Adapter we built for the Dashboard!
        reminderAdapter = DashboardRecyclerAdapter(
            getPrimary = { reminder ->
                if (reminder.isRead) reminder.title else "${reminder.title} • new"
            },
            getSecondary = { reminder -> reminder.description },
            onClick = { _, position ->
                presenter.markAsRead(position)
            },
            onLongClick = { _, position ->
                showReminderActions(position)
            }
        )

        recyclerView.adapter = reminderAdapter
    }

    private fun showReminderActions(position: Int) {
        val actions = arrayOf("Mark as read", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Reminder actions")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> presenter.markAsRead(position)
                    1 -> presenter.deleteReminder(position)
                }
            }
            .show()
    }

    override fun displayReminders(reminders: List<Reminder>) {
        reminderAdapter.submitList(reminders)
        findViewById<TextView>(R.id.textviewEmptyState).visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun updateUnreadCount(count: Int) {
        textviewUnreadCount.text = if (count > 0) "$count unread" else "All caught up"
    }

    override fun showEmptyState(isEmpty: Boolean) {
        textviewEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}