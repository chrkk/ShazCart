package com.shaz.shazcart.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
    private var settlementPayers: List<DashboardContract.SettlementEntry> = emptyList()
    private var settlementReceivers: List<DashboardContract.SettlementEntry> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        presenter = DashboardPresenter(this, DashboardModel(application as CustomApp))

        setupRecyclerViews()
        setupButtons()
        setupModeUI() // Applies Solo or Group logic dynamically

        presenter.loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        if (::presenter.isInitialized) {
            presenter.loadDashboard()
        }
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
            getSecondary = { housemate ->
                if (housemate.settlementPaid > 0.0) {
                    "${housemate.status} · Paid ₱${String.format("%.2f", housemate.settlementPaid)}"
                } else {
                    housemate.status
                }
            },
            onClick = { _, position ->
                showHousemateActionsDialog(position)
            },
            onLongClick = { _, position ->
                showRemoveHousemateDialog(position)
            }
        )

        groceryAdapter = DashboardRecyclerAdapter(
            getPrimary = { item -> item.itemName },
            getSecondary = { item -> "${item.assignedTo} — ${item.price}" },
            onClick = { item, _ ->
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
                showAddHousemateDialog()
            }
        }

        findViewById<Button>(R.id.buttonAddGrocery).setOnClickListener {
            showAddGroceryDialog()
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

        findViewById<TextView>(R.id.textviewNotification).setOnClickListener {
            val intent = Intent(this, com.shaz.shazcart.screens.reminder.ReminderActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonManageSplit).setOnClickListener {
            showSplitActionsDialog()
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

    private fun showAddHousemateDialog() {
        val input = EditText(this).apply {
            hint = "Housemate name"
        }

        AlertDialog.Builder(this)
            .setTitle("Add Housemate")
            .setMessage("Enter the housemate's name.")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    showMessage("Please enter a housemate name.")
                    return@setPositiveButton
                }
                presenter.addHousemate(name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showHousemateActionsDialog(position: Int) {
        val housemate = housematesAdapter.getItem(position) ?: return
        showHousematePaymentDialog(position, housemate)
    }

    private fun showHousematePaymentDialog(position: Int, housemate: Housemate) {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val statusText = TextView(this).apply {
            text = buildString {
                append(housemate.status.ifBlank { "No split yet" })
                if (housemate.settlementPaid > 0.0) {
                    append("\nAlready paid: ₱${String.format("%.2f", housemate.settlementPaid)}")
                }
            }
            setTextColor(android.graphics.Color.parseColor("#374151"))
        }

        val editButtonLabel = if (housemate.settlementPaid > 0.0) "Edit payment" else "Record payment"
        val editButton = Button(this).apply {
            text = editButtonLabel
            textColor = android.graphics.Color.WHITE
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#059669"))
            setOnClickListener {
                showEditHousematePaymentDialog(
                    position = position,
                    housemateName = housemate.name,
                    currentAmount = housemate.settlementPaid,
                    isEdit = housemate.settlementPaid > 0.0
                )
            }
        }

        val settleButton = Button(this).apply {
            text = "Settle full balance"
            textColor = android.graphics.Color.WHITE
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#3B82F6"))
            setOnClickListener {
                presenter.settleHousemate(position)
            }
        }

        val clearButton = Button(this).apply {
            text = "Clear payment"
            textColor = android.graphics.Color.WHITE
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#6B7280"))
            visibility = if (housemate.settlementPaid > 0.0) View.VISIBLE else View.GONE
            setOnClickListener {
                presenter.clearHousematePayment(position)
            }
        }

        val deleteButton = Button(this).apply {
            text = "Delete housemate"
            textColor = android.graphics.Color.WHITE
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#DC2626"))
            setOnClickListener {
                showRemoveHousemateDialog(position)
            }
        }

        container.addView(statusText)
        container.addView(editButton)
        container.addView(settleButton)
        container.addView(clearButton)
        container.addView(deleteButton)

        AlertDialog.Builder(this)
            .setTitle(housemate.name)
            .setView(container)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showEditHousematePaymentDialog(
        position: Int,
        housemateName: String,
        currentAmount: Double,
        isEdit: Boolean
    ) {
        val input = EditText(this).apply {
            hint = "Amount already paid"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (currentAmount > 0.0) {
                setText(String.format("%.2f", currentAmount))
                setSelection(text.length)
            }
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEdit) "Edit payment" else "Record payment")
            .setMessage(if (isEdit) "Update how much $housemateName has already paid." else "How much has $housemateName already paid?")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull()
                if (amount == null || amount <= 0.0) {
                    showMessage("Enter a valid amount.")
                    return@setPositiveButton
                }
                if (isEdit) {
                    presenter.setHousematePayment(position, amount)
                } else {
                    presenter.recordHousematePayment(position, amount)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSplitActionsDialog() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        fun addEntryButton(entry: DashboardContract.SettlementEntry) {
            val button = Button(this).apply {
                text = "${entry.name} · ₱${String.format("%.2f", entry.amount)}"
                isAllCaps = false
                textColor = android.graphics.Color.WHITE
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(if (entry.isPayer) "#DC2626" else "#059669")
                )
                setOnClickListener {
                    val housemate = housematesAdapter.getItem(entry.position)
                    if (housemate != null) {
                        showHousematePaymentDialog(entry.position, housemate)
                    }
                }
            }
            container.addView(button)
        }

        val header = TextView(this).apply {
            text = "Tap a name to open record, edit, settle, or clear actions."
            setTextColor(android.graphics.Color.parseColor("#6B7280"))
        }
        container.addView(header)

        if (settlementPayers.isEmpty() && settlementReceivers.isEmpty()) {
            container.addView(TextView(this).apply {
                text = "Everyone is settled."
                setTextColor(android.graphics.Color.parseColor("#111827"))
            })
        } else {
            if (settlementPayers.isNotEmpty()) {
                container.addView(TextView(this).apply {
                    text = "Needs to pay"
                    setTextColor(android.graphics.Color.parseColor("#DC2626"))
                })
                settlementPayers.forEach { addEntryButton(it) }
            }

            if (settlementReceivers.isNotEmpty()) {
                container.addView(TextView(this).apply {
                    text = "Should receive"
                    setTextColor(android.graphics.Color.parseColor("#059669"))
                    setPadding(0, 16, 0, 0)
                })
                settlementReceivers.forEach { addEntryButton(it) }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Split actions")
            .setView(container)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAddGroceryDialog() {
        val user = (application as CustomApp).getUser()

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val itemNameInput = EditText(this).apply {
            hint = "Item name"
        }

        val assignedToInput = EditText(this).apply {
            hint = "Buyer / assigned to"
            setText(if (user.mode == "Solo") user.username else "")
        }

        val priceInput = EditText(this).apply {
            hint = "Price"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        container.addView(itemNameInput)
        container.addView(assignedToInput)
        container.addView(priceInput)

        AlertDialog.Builder(this)
            .setTitle("Add Grocery Item")
            .setMessage("Assign a buyer before saving the item.")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val itemName = itemNameInput.text.toString().trim()
                val assignedTo = assignedToInput.text.toString().trim()
                val priceValue = priceInput.text.toString().trim()

                if (itemName.isEmpty() || assignedTo.isEmpty() || priceValue.isEmpty()) {
                    showMessage("Please fill in item name, buyer, and price.")
                    return@setPositiveButton
                }

                val normalizedPrice = if (priceValue.startsWith("₱")) priceValue else "₱$priceValue"
                presenter.addGroceryItem(itemName, assignedTo, normalizedPrice)
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

    override fun showSettlementSummary(needsToPay: String, shouldReceive: String) {
        findViewById<TextView>(R.id.textviewNeedsToPay).text = needsToPay
        findViewById<TextView>(R.id.textviewShouldReceive).text = shouldReceive
    }

    override fun showSettlementEntries(
        payers: List<DashboardContract.SettlementEntry>,
        receivers: List<DashboardContract.SettlementEntry>
    ) {
        settlementPayers = payers
        settlementReceivers = receivers

        findViewById<Button>(R.id.buttonManageSplit).text =
            if (payers.isEmpty() && receivers.isEmpty()) {
                "All settled"
            } else {
                "Open split actions (${payers.size + receivers.size})"
            }
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

    override fun updateNotificationBadge(count: Int) {
        val textviewNotification = findViewById<TextView>(R.id.textviewNotification)
        if (count > 0) {
            textviewNotification.text = "🔔 $count"
        } else {
            textviewNotification.text = "🔔"
        }
    }
}