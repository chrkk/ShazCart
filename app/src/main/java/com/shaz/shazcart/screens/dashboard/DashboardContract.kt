package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

interface DashboardContract {
    data class SettlementEntry(
        val name: String,
        val amount: Double,
        val isPayer: Boolean,
        val position: Int
    )

    interface View {
        fun showSummary(totalItems: Int, pendingItems: Int, totalSpent: Double)
        fun showSettlementSummary(needsToPay: String, shouldReceive: String)
        fun showSettlementEntries(payers: List<SettlementEntry>, receivers: List<SettlementEntry>)
        fun showHousematesStatus(housemates: List<Housemate>)
        fun showSharedList(items: List<GroceryItem>)
        fun showMessage(message: String)
        fun showBudgetWarning(message: String, isCritical: Boolean) // NEW
        fun updateNotificationBadge(count: Int) // NEW
    }

    interface Presenter {
        fun loadDashboard()
        fun addHousemate(name: String)
        fun removeHousemate(position: Int)
        fun recordHousematePayment(position: Int, amount: Double)
        fun setHousematePayment(position: Int, amount: Double)
        fun settleHousemate(position: Int)
        fun settleAllBalances()
        fun clearHousematePayment(position: Int)
        fun addGroceryItem(itemName: String, assignedTo: String, price: String)
        fun removeGroceryItem(position: Int)
        fun updateBudget(amount: Double) // NEW
        fun recordSettlement(from: String, to: String, amount: Double)
    }
}