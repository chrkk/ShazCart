package com.shaz.shazcart.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate
import com.shaz.shazcart.helper.DashboardRecyclerAdapter
import com.shaz.shazcart.screens.welcome.WelcomeActivity
import com.shaz.shazcart.utils.showModeSwitchDialog

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
        val housematesTitle = findViewById<TextView>(R.id.textviewHousematesTitle)
        val housematesDescription = findViewById<TextView>(R.id.textviewHousematesDescription)
        val sharedListTitle = findViewById<TextView>(R.id.textviewSharedListTitle)
        val sharedListDescription = findViewById<TextView>(R.id.textviewSharedListDescription)
        val splitCard = findViewById<View>(R.id.cardSplitOverview)
        val personalSummaryCard = findViewById<View>(R.id.personalSummaryCard)

        if (user.mode == "Solo") {
            textviewGroupMode.text = "Solo Mode — Personal Dashboard"
            housematesTitle.visibility = View.VISIBLE
            housematesTitle.text = "Budget Limit"
            housematesDescription.text = "Set your spending cap for personal mode."
            findViewById<RecyclerView>(R.id.recyclerviewHousemates).visibility = View.GONE
            splitCard.visibility = View.GONE
            personalSummaryCard.visibility = View.VISIBLE
            buttonAddHousemate.text = "Set Budget Limit"
            buttonAddHousemate.setBackgroundColor(ContextCompat.getColor(this, R.color.dashboard_accent))
            sharedListTitle.text = "Personal Grocery List"
            sharedListDescription.text = "Track what you buy for yourself and keep it tidy."
        } else {
            textviewGroupMode.text = "Group Mode — Shared Boarding House"
            housematesTitle.visibility = View.VISIBLE
            housematesTitle.text = "Housemates"
            housematesDescription.text = "Tap a housemate to manage payment status."
            findViewById<RecyclerView>(R.id.recyclerviewHousemates).visibility = View.VISIBLE
            buttonAddHousemate.text = "＋ Add Housemate"
            buttonAddHousemate.setBackgroundColor(ContextCompat.getColor(this, R.color.dashboard_success))
            sharedListTitle.text = "Shared Grocery List"
            sharedListDescription.text = "Assign items and keep the shared list tidy."
            splitCard.visibility = View.VISIBLE
            personalSummaryCard.visibility = View.GONE
        }
    }

    private fun setupRecyclerViews() {
        val housematesRecyclerView = findViewById<RecyclerView>(R.id.recyclerviewHousemates)
        val groceryRecyclerView = findViewById<RecyclerView>(R.id.recyclerviewGrocery)

        housematesRecyclerView.layoutManager = LinearLayoutManager(this)
        groceryRecyclerView.layoutManager = LinearLayoutManager(this)

        housematesAdapter = DashboardRecyclerAdapter(
            getPrimary = { housemate ->
                val currentUser = (application as CustomApp).getUser()
                if (currentUser.mode == "Group" && housemate.name == currentUser.displayName) {
                    "You"
                } else {
                    housemate.name
                }
            },
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
                val housemate = housematesAdapter.getItem(position)
                val currentUser = (application as CustomApp).getUser()
                if (housemate != null && currentUser.mode == "Group" && housemate.name == currentUser.displayName) {
                    showMessage("You are included automatically.")
                } else {
                    showRemoveHousemateDialog(position)
                }
            }
        )

        groceryAdapter = DashboardRecyclerAdapter(
            getPrimary = { item -> item.itemName },
            getSecondary = { item ->
                val currentUser = (application as CustomApp).getUser()
                val payerLabel = if (currentUser.mode == "Group" && item.assignedTo == currentUser.displayName) {
                    "You"
                } else {
                    item.assignedTo
                }

                if (currentUser.mode == "Group") {
                    "Paid by $payerLabel — ${item.price}"
                } else {
                    "$payerLabel — ${item.price}"
                }
            },
            onClick = { item, _ ->
                val currentUser = (application as CustomApp).getUser()
                val payerLabel = if (currentUser.mode == "Group" && item.assignedTo == currentUser.displayName) {
                    "You"
                } else {
                    item.assignedTo
                }
                val actionLabel = if (currentUser.mode == "Group") "paid by" else "bought by"
                Toast.makeText(this, "${item.itemName} $actionLabel $payerLabel for ${item.price}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { _, position ->
                showRemoveGroceryDialog(position)
            }
        )

        housematesRecyclerView.adapter = housematesAdapter
        groceryRecyclerView.adapter = groceryAdapter
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonAddHousemate).setOnClickListener {
            val currentUser = (application as CustomApp).getUser()
            if (currentUser.mode == "Solo") {
                showSetBudgetDialog()
            } else {
                showAddHousemateDialog()
            }
        }

        findViewById<Button>(R.id.buttonAddGrocery).setOnClickListener {
            val currentUser = (application as CustomApp).getUser()
            if (currentUser.mode == "Solo") {
                showAddGroceryDialog()
            } else {
                showAddSharedExpenseDialog()
            }
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

        findViewById<Button>(R.id.buttonSwitchMode).setOnClickListener {
            val currentMode = (application as CustomApp).getUser().mode
            showModeSwitchDialog(currentMode) { selected ->
                (application as CustomApp).updateUserMode(selected)
                setupModeUI()
                presenter.loadDashboard()
                showMessage("Mode switched to $selected")
            }
        }
    }

    private fun showRecordSettlementDialog() {
        val names = housematesAdapter.getAllItems().map { (it as com.shaz.shazcart.data.Housemate).name }
        if (names.size < 2) {
            showMessage("Need at least two housemates to record a transfer.")
            return
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val fromInput = AutoCompleteTextView(this).apply {
            hint = "From (payer)"
            threshold = 1
            setAdapter(ArrayAdapter(this@DashboardActivity, android.R.layout.simple_dropdown_item_1line, names))
        }

        val toInput = AutoCompleteTextView(this).apply {
            hint = "To (receiver)"
            threshold = 1
            setAdapter(ArrayAdapter(this@DashboardActivity, android.R.layout.simple_dropdown_item_1line, names))
        }

        val amountInput = EditText(this).apply {
            hint = "Amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (user.mode == "Solo") {
                showAddGroceryDialog()
            } else {
                showAddSharedExpenseDialog()
            }
        }

        container.addView(fromInput)
        container.addView(toInput)
        container.addView(amountInput)

        AlertDialog.Builder(this)
            .setTitle("Record transfer")
            .setMessage("Record a payment transfer between housemates.")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val from = fromInput.text.toString().trim()
                val to = toInput.text.toString().trim()
                val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            findViewById<Button>(R.id.buttonAddGrocery).text = "＋ Add Grocery Item"

                if (from.isEmpty() || to.isEmpty() || amount <= 0.0) {
                    showMessage("Please provide payer, receiver, and amount.")
                    return@setPositiveButton
                }
                if (from == to) {
                    showMessage("Payer and receiver must be different.")
                    return@setPositiveButton
            sharedListTitle.text = "Shared Expenses"
            sharedListDescription.text = "Record groceries, bills, and split costs across housemates."
            findViewById<Button>(R.id.buttonAddGrocery).text = "＋ Add Expense"
                (presenter as DashboardContract.Presenter).recordSettlement(from, to, amount)
            }
            .setNegativeButton("Cancel", null)
            .show()
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
                val currentUserName = (application as CustomApp).getUser().displayName
                if (name == currentUserName) {
                    showMessage("You are already included automatically.")
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
                if (housemate.settlementReceived > 0.0) {
                    append("\nAlready received: ₱${String.format("%.2f", housemate.settlementReceived)}")
                }
            }
            setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_text_muted))
        }

        val editButtonLabel = if (housemate.settlementPaid > 0.0) "Edit payment" else "Record payment"
        val editButton = Button(this).apply {
            text = editButtonLabel
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_success)
            )
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
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_accent)
            )
            setOnClickListener {
                presenter.settleHousemate(position)
            }
        }

        val clearButton = Button(this).apply {
            text = "Clear payment"
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_text_muted)
            )
            visibility = if (housemate.settlementPaid > 0.0 || housemate.settlementReceived > 0.0) View.VISIBLE else View.GONE
            setOnClickListener {
                presenter.clearHousematePayment(position)
            }
        }

        val deleteButton = Button(this).apply {
            text = "Delete housemate"
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_danger)
            )
            val currentUser = (application as CustomApp).getUser()
            visibility = if (currentUser.mode == "Group" && housemate.name == currentUser.displayName) View.GONE else View.VISIBLE
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
            .setTitle(
                if ((application as CustomApp).getUser().mode == "Group" &&
                    housemate.name == (application as CustomApp).getUser().displayName
                ) "You" else housemate.name
            )
            .setView(container)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAddSharedExpenseDialog() {
        val user = (application as CustomApp).getUser()
        val housemateNames = housematesAdapter.getAllItems().map { (it as Housemate).name }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val expenseNameInput = EditText(this).apply {
            hint = "Expense name"
        }

        val paidByInput = AutoCompleteTextView(this).apply {
            hint = "Paid by"
            threshold = 1
            setAdapter(ArrayAdapter(this@DashboardActivity, android.R.layout.simple_dropdown_item_1line, housemateNames))
            setText(user.displayName, false)
        }

        val amountInput = EditText(this).apply {
            hint = "Total amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        container.addView(expenseNameInput)
        container.addView(paidByInput)
        container.addView(amountInput)

        AlertDialog.Builder(this)
            .setTitle("Add Shared Expense")
            .setMessage("The amount will be split equally among all housemates, including you.")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val expenseName = expenseNameInput.text.toString().trim()
                val paidBy = paidByInput.text.toString().trim()
                val amountValue = amountInput.text.toString().trim()

                if (expenseName.isEmpty() || paidBy.isEmpty() || amountValue.isEmpty()) {
                    showMessage("Please fill in expense name, paid by, and amount.")
                    return@setPositiveButton
                }

                if (!housemateNames.contains(paidBy)) {
                    showMessage("Please choose a valid housemate.")
                    return@setPositiveButton
                }

                val normalizedAmount = if (amountValue.startsWith("₱")) amountValue else "₱$amountValue"
                presenter.addGroceryItem(expenseName, paidBy, normalizedAmount)
            }
            .setNegativeButton("Cancel", null)
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
                setTextColor(android.graphics.Color.WHITE)
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@DashboardActivity,
                        if (entry.isPayer) R.color.dashboard_danger else R.color.dashboard_success
                    )
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
            text = "Tap a name to review, settle, or clear a payment."
            setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_text_muted))
        }
        container.addView(header)

        if (settlementPayers.isEmpty() && settlementReceivers.isEmpty()) {
            container.addView(TextView(this).apply {
                text = "Everyone is settled."
                setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_text))
            })
            // Add a quick 'Record transfer' CTA even when settled
            container.addView(Button(this).apply {
                text = "Record transfer"
                setOnClickListener { showRecordSettlementDialog() }
            })
        } else {
            if (settlementPayers.isNotEmpty()) {
                container.addView(TextView(this).apply {
                    text = "Needs to pay"
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_danger))
                })
                settlementPayers.forEach { addEntryButton(it) }
            }

            if (settlementReceivers.isNotEmpty()) {
                container.addView(TextView(this).apply {
                    text = "Should receive"
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.dashboard_success))
                    setPadding(0, 16, 0, 0)
                })
                settlementReceivers.forEach { addEntryButton(it) }
            }
            // Add a Record transfer button below entries
            container.addView(Button(this).apply {
                text = "Record transfer"
                setOnClickListener { showRecordSettlementDialog() }
            })
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

        // Build assigned-to input: free text in Solo, autocomplete with unassigned in Group
        val assignedToInputView: View = if (user.mode == "Solo") {
            EditText(this).apply {
                hint = "Buyer / assigned to"
                setText(user.displayName)
            }
        } else {
            val names = buildList {
                add("Unassigned")
                addAll(housematesAdapter.getAllItems().map { (it as com.shaz.shazcart.data.Housemate).name })
            }.distinct()
            AutoCompleteTextView(this).apply {
                hint = "Buyer / assigned to"
                threshold = 1
                setAdapter(ArrayAdapter(this@DashboardActivity, android.R.layout.simple_dropdown_item_1line, names))
                setText("Unassigned", false)
            }
        }

        val priceInput = EditText(this).apply {
            hint = "Price"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        container.addView(itemNameInput)
        container.addView(assignedToInputView)
        container.addView(priceInput)

        AlertDialog.Builder(this)
            .setTitle("Add Grocery Item")
            .setMessage(if (user.mode == "Solo") {
                "Enter your item details and save."
            } else {
                "Assign a housemate now or leave it unassigned for later."
            })
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val itemName = itemNameInput.text.toString().trim()
                val assignedTo = when (assignedToInputView) {
                    is EditText -> assignedToInputView.text.toString().trim()
                    is AutoCompleteTextView -> assignedToInputView.text.toString().trim()
                    else -> ""
                }
                val priceValue = priceInput.text.toString().trim()

                val normalizedAssignedTo = if (assignedTo.isBlank()) {
                    if (user.mode == "Solo") user.displayName else "Unassigned"
                } else {
                    assignedTo
                }

                if (itemName.isEmpty() || priceValue.isEmpty()) {
                    showMessage("Please fill in item name and price.")
                    return@setPositiveButton
                }

                if (user.mode != "Solo") {
                    val validNames = buildList {
                        add("Unassigned")
                        addAll(housematesAdapter.getAllItems().map { (it as com.shaz.shazcart.data.Housemate).name })
                    }
                    if (!validNames.contains(normalizedAssignedTo)) {
                        showMessage("Please choose a housemate or Unassigned.")
                        return@setPositiveButton
                    }
                }

                val normalizedPrice = if (priceValue.startsWith("₱")) priceValue else "₱$priceValue"
                presenter.addGroceryItem(itemName, normalizedAssignedTo, normalizedPrice)
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
        findViewById<TextView>(R.id.textviewTotalItems).text = "$totalItems items"
        findViewById<TextView>(R.id.textviewPendingItems).text = if (pendingItems > 0) {
            "$pendingItems awaiting assignment"
        } else {
            "Fully assigned"
        }
        findViewById<TextView>(R.id.textviewTotalSpent).text = "₱${String.format("%.2f", totalSpent)} spent"
        // If we're in Solo mode, update the personal summary card numbers
        val user = (application as CustomApp).getUser()
        if (user.mode == "Solo") {
            findViewById<TextView>(R.id.textviewBudgetLimit).text = "₱${String.format("%.2f", user.budgetLimit)}"
            val remaining = user.budgetLimit - totalSpent
            findViewById<TextView>(R.id.textviewBudgetRemaining).text = "₱${String.format("%.2f", remaining)}"
        }
    }

    override fun showSettlementSummary(needsToPay: String, shouldReceive: String) {
        if ((application as CustomApp).getUser().mode == "Solo") {
            return
        }
        findViewById<TextView>(R.id.textviewNeedsToPay).text = needsToPay
        findViewById<TextView>(R.id.textviewShouldReceive).text = shouldReceive
    }

    override fun showSettlementEntries(
        payers: List<DashboardContract.SettlementEntry>,
        receivers: List<DashboardContract.SettlementEntry>
    ) {
        if ((application as CustomApp).getUser().mode == "Solo") {
            settlementPayers = emptyList()
            settlementReceivers = emptyList()
            return
        }
        settlementPayers = payers
        settlementReceivers = receivers

        findViewById<Button>(R.id.buttonManageSplit).text =
            if (payers.isEmpty() && receivers.isEmpty()) {
                "All settled"
            } else {
                "Review balances (${payers.size + receivers.size})"
            }
    }

    override fun showHousematesStatus(housemates: List<Housemate>) {
        housematesAdapter.submitList(housemates)
    }

    override fun showSharedList(items: List<GroceryItem>) {
        // In Solo mode, only show grocery items assigned to the current user.
        val user = (application as CustomApp).getUser()
        val listToShow = if (user.mode == "Solo") {
            items.filter { it.assignedTo == user.displayName }
        } else {
            items
        }
        groceryAdapter.submitList(listToShow)
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