package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

interface DashboardContract {
    interface View {
        fun showSummary(totalItems: Int, pendingItems: Int, totalSpent: Double)
        fun showHousematesStatus(housemates: MutableList<Housemate>)
        fun showSharedList(items: MutableList<GroceryItem>)
    }

    interface Presenter {
        fun loadDashboard()
    }
}
