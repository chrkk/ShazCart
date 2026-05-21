package com.shaz.shazcart.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate
import com.shaz.shazcart.helper.DashboardRecyclerAdapter
import com.shaz.shazcart.screens.welcome.WelcomeActivity

class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var presenter: DashboardPresenter
    private lateinit var housematesAdapter: DashboardRecyclerAdapter<Housemate>
    private lateinit var groceryAdapter: DashboardRecyclerAdapter<GroceryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        presenter = DashboardPresenter(this, DashboardModel(application as CustomApp))

        setupRecyclerViews()
        setupButtons()
        setupModeUI() // Applies Solo or Group logic dynamically

        presenter.loadDashboard()
    }

    private fun setupModeUI() {
        val user = (application as CustomApp).getUser()
        val textviewGroupMode = findViewById<TextView>(R.id.textviewGroupMode)
        val buttonAddHousemate = findViewById<Button>(R.id.buttonAddHousemate)

        if (user.mode == "Solo") {
            // Adjust the header text
            textviewGroupMode.text = "Solo Mode — Personal Dashboard"

            // Hide housemates section completely
            findViewById<TextView>(R.id.textviewHousematesTitle).visibility = View.GONE
            findViewById<RecyclerView>(R.id.recyclerviewHousemates).visibility = View.GONE

            // Repurpose the housemate button into a Budget Limit setter
            buttonAddHousemate.text = "Set Budget Limit"
            buttonAddHousemate.setBackgroundColor(android.graphics.Color.parseColor("#3B82F6")) // Blue

            // Rename Shared Grocery List to Personal
            findViewById<TextView>(R.id.textviewSharedListTitle).text = "Personal Grocery List"
        } else {
            textviewGroupMode.text = "Group Mode — Shared Boarding House"
        }
    }

    private fun setupRecyclerViews() {
        val housematesRecyclerView = findViewById<RecyclerView>(R.id.recyclerviewHousemates)
        val groceryRecyclerView = findViewById<RecyclerView>(R.id.recyclerviewGrocery)

        housematesRecyclerView.layoutManager = LinearLayoutManager(this)
        groceryRecyclerView.layoutManager = LinearLayoutManager(this)

        housematesAdapter = DashboardRecyclerAdapter(
            getPrimary = { housemate -> housemate.name },
            getSecondary = { housemate -> housemate.status },
            onClick = { housemate ->
                Toast.makeText(this, "${housemate.name}: ${housemate.status}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { _, position ->
                showRemoveHousemateDialog(position)
            }
        )

        groceryAdapter = DashboardRecyclerAdapter(
            getPrimary = { item -> item.itemName },
            getSecondary = { item -> "${item.assignedTo} — ${item.price}" },
            onClick = { item ->
                Toast.makeText(this, "${item.itemName} bought by ${item.assignedTo} for ${item.price}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { _, position ->
                showRemoveGroceryDialog(position)
            }
        )

        housematesRecyclerView.adapter = housematesAdapter
        groceryRecyclerView.adapter = groceryAdapter
    }

    private fun setupButtons() {
        val user = (application as CustomApp).getUser()

        findViewById<Button>(R.id.buttonAddHousemate).setOnClickListener {
            if (user.mode == "Solo") {
                showSetBudgetDialog()
            } else {
                presenter.addHousemate()
            }
        }

        findViewById<Button>(R.id.buttonAddGrocery).setOnClickListener {
            presenter.addGroceryItem()
        }

        findViewById<Button>(R.id.buttonLogout)?.setOnClickListener {
            (application as CustomApp).clearUser()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.buttonProfile)?.setOnClickListener {
            val intent = Intent(this, com.shaz.shazcart.screens.profile.ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSetBudgetDialog() {
        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        AlertDialog.Builder(this)
            .setTitle("Set Budget Limit")
            .setMessage("Enter your maximum spending limit:")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull() ?: 0.0
                presenter.updateBudget(amount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveHousemateDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Remove Housemate")
            .setMessage("Are you sure you want to remove this housemate?")
            .setPositiveButton("Remove") { _, _ -> presenter.removeHousemate(position) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveGroceryDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove this item?")
            .setPositiveButton("Remove") { _, _ -> presenter.removeGroceryItem(position) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- DashboardContract.View implementations ---

    override fun showSummary(totalItems: Int, pendingItems: Int, totalSpent: Double) {
        findViewById<TextView>(R.id.textviewTotalItems).text = "$totalItems Items"
        findViewById<TextView>(R.id.textviewPendingItems).text = "$pendingItems Pending"
        findViewById<TextView>(R.id.textviewTotalSpent).text = "₱$totalSpent Spent"
    }

    override fun showHousematesStatus(housemates: List<Housemate>) {
        housematesAdapter.submitList(housemates)
    }

    override fun showSharedList(items: List<GroceryItem>) {
        groceryAdapter.submitList(items)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showBudgetWarning(message: String, isCritical: Boolean) {
        val textviewNotification = findViewById<TextView>(R.id.textviewNotification)

        // Show an alert dialog for critical over-budget situations
        if (isCritical) {
            AlertDialog.Builder(this)
                .setTitle("Budget Exceeded!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
            textviewNotification.text = "🔴 Over Budget"
        } else {
            // Passive warning
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            textviewNotification.text = "⚠️ Near Budget"
        }
    }
}