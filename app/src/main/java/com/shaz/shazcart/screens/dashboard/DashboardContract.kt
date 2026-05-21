package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

interface DashboardContract {
    interface View {
        fun showSummary(totalItems: Int, pendingItems: Int, totalSpent: Double)
        fun showHousematesStatus(housemates: List<Housemate>)
        fun showSharedList(items: List<GroceryItem>)
        fun showMessage(message: String)
        fun showBudgetWarning(message: String, isCritical: Boolean) // NEW
    }

    interface Presenter {
        fun loadDashboard()
        fun addHousemate()
        fun removeHousemate(position: Int)
        fun addGroceryItem()
        fun removeGroceryItem(position: Int)
        fun updateBudget(amount: Double) // NEW
    }
}