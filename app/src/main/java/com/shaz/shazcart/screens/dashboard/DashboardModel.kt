package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

class DashboardModel(private val app: CustomApp) {

    fun getSummary(): Triple<Int, Int, Double> {
        return Triple(6, 4, 1800.0)
    }
    fun getHousematesStatus(): MutableList<Housemate> {
        return mutableListOf(
            Housemate("Marco", "Settled ✅"),
            Housemate("Lea", "Owes ₱80 ⚠️"),
            Housemate("Juan", "Owes ₱240 ⚠️"),
            Housemate("Ana", "Settled ✅")
        )
    }

    fun getSharedList(): MutableList<GroceryItem> {
        return mutableListOf(
            GroceryItem("Rice (5kg)", "Marco", "₱280"),
            GroceryItem("Cooking Oil", "Lea", "₱95"),
            GroceryItem("Eggs", "Juan", "₱180"),
            GroceryItem("Instant Noodles", "Marco", "₱120")
        )
    }
}
