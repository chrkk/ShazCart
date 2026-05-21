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

    override fun addHousemate() {
        model.addHousemate(Housemate("New Housemate", "Owes ₱0 ⚠️"))
        view.showMessage("Housemate has been added.")
        refreshView()
    }

    override fun removeHousemate(position: Int) {
        val removed = model.removeHousemate(position)
        view.showMessage("${removed.name} has been removed.")
        refreshView()
    }

    override fun addGroceryItem() {
        model.addGroceryItem(GroceryItem("New Item", "Unassigned", "₱0"))
        view.showMessage("Grocery item has been added.")
        refreshView()
    }

    override fun removeGroceryItem(position: Int) {
        val removed = model.removeGroceryItem(position)
        view.showMessage("${removed.itemName} has been removed.")
        refreshView()
    }

    private fun refreshView() {
        val (totalItems, pendingItems, totalSpent) = model.getSummary()
        view.showSummary(totalItems, pendingItems, totalSpent)
        view.showHousematesStatus(model.getHousematesStatus().toList())
        view.showSharedList(model.getSharedList().toList())
    }
}