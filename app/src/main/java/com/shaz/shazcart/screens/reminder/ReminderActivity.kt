package com.shaz.shazcart.screens.reminder

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        presenter = ReminderPresenter(this, ReminderModel(application as CustomApp))

        setupRecyclerView()

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
                if (reminder.isRead) "[READ] ${reminder.title}" else "🔴 ${reminder.title}" 
            },
            getSecondary = { reminder -> reminder.description },
            onClick = { reminder -> 
                Toast.makeText(this, reminder.description, Toast.LENGTH_SHORT).show()
            },
            onLongClick = { _, position ->
                presenter.markAsRead(position)
            }
        )

        recyclerView.adapter = reminderAdapter
    }

    override fun displayReminders(reminders: List<Reminder>) {
        reminderAdapter.submitList(reminders)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}