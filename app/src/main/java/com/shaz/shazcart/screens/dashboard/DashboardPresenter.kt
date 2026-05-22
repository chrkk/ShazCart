package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        refreshView()
    }

    override fun addHousemate(name: String) {
        model.addHousemate(Housemate(name, 0.0, ""))
        view.showMessage("$name has been added.")
        refreshView()
    }

    override fun removeHousemate(position: Int) {
        val removed = model.removeHousemate(position)
        view.showMessage("${removed.name} has been removed.")
        refreshView()
    }

    override fun recordHousematePayment(position: Int, amount: Double) {
        val updated = model.recordHousematePayment(position, amount)
        view.showMessage("${updated.name} payment recorded.")
        refreshView()
    }

    override fun setHousematePayment(position: Int, amount: Double) {
        val updated = model.setHousematePayment(position, amount)
        view.showMessage("${updated.name} payment updated.")
        refreshView()
    }

    override fun settleHousemate(position: Int) {
        val updated = model.settleHousemate(position)
        view.showMessage("${updated.name} marked as settled.")
        refreshView()
    }

    override fun clearHousematePayment(position: Int) {
        val updated = model.clearHousematePayment(position)
        view.showMessage("${updated.name} payment cleared.")
        refreshView()
    }

    override fun addGroceryItem(itemName: String, assignedTo: String, price: String) {
        model.addGroceryItem(GroceryItem(itemName, assignedTo, price))
        view.showMessage("$itemName has been added.")
        refreshView()
    }

    override fun removeGroceryItem(position: Int) {
        val removed = model.removeGroceryItem(position)
        view.showMessage("${removed.itemName} has been removed.")
        refreshView()
    }

    override fun updateBudget(amount: Double) {
        model.setBudgetLimit(amount)
        view.showMessage("Budget limit updated to ₱$amount")
        refreshView()
    }

    override fun recordSettlement(from: String, to: String, amount: Double) {
        model.addSettlement(from, to, amount)
        view.showMessage("Recorded transfer ₱$amount from $from to $to")
        refreshView()
    }

    private fun refreshView() {
        if (model.getMode() == "Group") {
            model.ensureCurrentUserIncluded()
        }

        val (totalItems, pendingItems, totalSpent) = model.getSummary()
        view.showSummary(totalItems, pendingItems, totalSpent)
        if (model.getMode() == "Group") {
            val (needsToPay, shouldReceive) = model.getSettlementSummary()
            view.showSettlementSummary(needsToPay, shouldReceive)
            val (payers, receivers) = model.getSettlementEntries()
            view.showSettlementEntries(payers, receivers)
        } else {
            view.showSettlementSummary("", "")
            view.showSettlementEntries(emptyList(), emptyList())
        }
        view.showHousematesStatus(model.getHousematesStatus().toList())
        view.showSharedList(model.getSharedList().toList())

        // Reminders Badge evaluation
        view.updateNotificationBadge(model.getUnreadRemindersCount())

        // Budget evaluation
        if (model.getMode() == "Solo") {
            val budget = model.getBudgetLimit()
            if (budget > 0) {
                if (totalSpent > budget) {
                    view.showBudgetWarning("CRITICAL: You are over your budget of ₱$budget!", true)
                } else if (totalSpent >= budget * 0.8) {
                    view.showBudgetWarning("Warning: You are close to your budget of ₱$budget.", false)
                }
            }
        }
    }
}