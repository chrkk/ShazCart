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

    private fun refreshView() {
        val (totalItems, pendingItems, totalSpent) = model.getSummary()
        view.showSummary(totalItems, pendingItems, totalSpent)
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