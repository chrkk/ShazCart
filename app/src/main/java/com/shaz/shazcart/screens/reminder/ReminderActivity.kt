package com.shaz.shazcart.screens.reminder

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
                if (reminder.isRead) "${reminder.title}" else "${reminder.title}"
            },
            getSecondary = { reminder -> reminder.description },
            onClick = { reminder -> 
                val position = reminderAdapterPosition(reminder)
                if (position >= 0 && !reminder.isRead) {
                    presenter.markAsRead(position)
                } else {
                    Toast.makeText(this, reminder.description, Toast.LENGTH_SHORT).show()
                }
            },
            onLongClick = { _, position ->
                showReminderActions(position)
            }
        )

        recyclerView.adapter = reminderAdapter
    }

    private fun reminderAdapterPosition(reminder: Reminder): Int {
        return reminderAdapterPositionInternal(reminder)
    }

    private fun reminderAdapterPositionInternal(reminder: Reminder): Int {
        return reminderAdapterPositionFromList(reminder)
    }

    private fun reminderAdapterPositionFromList(reminder: Reminder): Int {
        return (findViewById<RecyclerView>(R.id.recyclerviewReminders).adapter as? DashboardRecyclerAdapter<Reminder>)
            ?.let { adapter ->
                val current = (0 until adapter.itemCount).firstOrNull { index ->
                    val text = reminder.title
                    index >= 0 && text.isNotBlank()
                }
                current ?: -1
            } ?: -1
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