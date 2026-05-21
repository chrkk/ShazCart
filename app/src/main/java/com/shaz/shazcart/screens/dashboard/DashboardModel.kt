package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

class DashboardModel(private val app: CustomApp) {

    private val housemates = mutableListOf(
        Housemate("Marco", "Settled ✅"),
        Housemate("Lea", "Owes ₱80 ⚠️"),
        Housemate("Juan", "Owes ₱240 ⚠️"),
        Housemate("Ana", "Settled ✅")
    )

    private val groceryList = mutableListOf(
        GroceryItem("Rice (5kg)", "Marco", "₱280"),
        GroceryItem("Cooking Oil", "Lea", "₱95"),
        GroceryItem("Eggs", "Juan", "₱180"),
        GroceryItem("Instant Noodles", "Marco", "₱120")
    )

    fun getSummary(): Triple<Int, Int, Double> {
        val totalItems = groceryList.size
        val pendingItems = groceryList.count { it.assignedTo == "Unassigned" }

        var totalSpent = 0.0
        for (item in groceryList) {
            val priceStr = item.price.replace("₱", "").replace(",", "").trim()
            val price = priceStr.toDoubleOrNull() ?: 0.0
            totalSpent += price
        }
        return Triple(totalItems, pendingItems, totalSpent)
    }

    fun getHousematesStatus(): List<Housemate> = housemates
    fun getSharedList(): List<GroceryItem> = groceryList

    fun addHousemate(housemate: Housemate) {
        housemates.add(housemate)
    }

    fun removeHousemate(position: Int): Housemate {
        return housemates.removeAt(position)
    }

    fun addGroceryItem(item: GroceryItem) {
        groceryList.add(item)
    }

    fun removeGroceryItem(position: Int): GroceryItem {
        return groceryList.removeAt(position)
    }
}