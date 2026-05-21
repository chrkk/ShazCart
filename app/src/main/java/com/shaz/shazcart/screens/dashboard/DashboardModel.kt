package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate

class DashboardModel(private val app: CustomApp) {

    private val housemates = mutableListOf(
        Housemate("Marco", 0.0, ""),
        Housemate("Lea", 0.0, ""),
        Housemate("Juan", 0.0, ""),
        Housemate("Ana", 0.0, "")
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

    private fun updateExpenseSplit() {
        if (housemates.isEmpty()) return

        val (_, _, totalSpent) = getSummary()
        val splitAmount = totalSpent / housemates.size

        // 1. Calculate how much each housemate has ALREADY paid based on assigned items
        val paidAmounts = mutableMapOf<String, Double>()
        for (housemate in housemates) {
            paidAmounts[housemate.name] = 0.0
        }

        for (item in groceryList) {
            val priceStr = item.price.replace("₱", "").replace(",", "").trim()
            val price = priceStr.toDoubleOrNull() ?: 0.0
            val currentPaid = paidAmounts[item.assignedTo] ?: 0.0
            paidAmounts[item.assignedTo] = currentPaid + price
        }

        // 2. Compare what they should pay (splitAmount) vs what they already paid
        for (housemate in housemates) {
            val paid = paidAmounts[housemate.name] ?: 0.0
            val balance = splitAmount - paid - housemate.settlementPaid

            if (balance > 0.01) {
                housemate.amountOwed = balance
                housemate.status = "Owes ₱${String.format("%.2f", balance)} 🔴"
            } else if (balance < -0.01) {
                housemate.amountOwed = 0.0
                housemate.status = "Overpaid ₱${String.format("%.2f", -balance)} 🟢"
            } else {
                housemate.amountOwed = 0.0
                housemate.status = "Settled ✅"
            }
        }
    }

    fun getHousematesStatus(): List<Housemate> {
        updateExpenseSplit() // Recalculate dependencies before returning
        return housemates
    }

    fun getSharedList(): List<GroceryItem> = groceryList

    fun addHousemate(housemate: Housemate) {
        housemates.add(housemate)
    }

    fun removeHousemate(position: Int): Housemate {
        return housemates.removeAt(position)
    }

    fun recordHousematePayment(position: Int, amount: Double): Housemate {
        housemates[position].settlementPaid += amount
        updateExpenseSplit()
        return housemates[position]
    }

    fun settleHousemate(position: Int): Housemate {
        updateExpenseSplit()
        val housemate = housemates[position]
        if (housemate.amountOwed > 0.0) {
            housemate.settlementPaid += housemate.amountOwed
        }
        updateExpenseSplit()
        return housemate
    }

    fun clearHousematePayment(position: Int): Housemate {
        housemates[position].settlementPaid = 0.0
        updateExpenseSplit()
        return housemates[position]
    }

    fun addGroceryItem(item: GroceryItem) {
        groceryList.add(item)
    }

    fun getHousemateNames(): List<String> {
        return housemates.map { it.name }
    }

    fun removeGroceryItem(position: Int): GroceryItem {
        return groceryList.removeAt(position)
    }

    fun getBudgetLimit(): Double {
        return app.getUser().budgetLimit
    }

    fun setBudgetLimit(limit: Double) {
        val user = app.getUser()
        user.budgetLimit = limit
        app.setUser(user)
    }

    fun getMode(): String {
        return app.getUser().mode
    }
    
    fun getUnreadRemindersCount(): Int {
        return app.getUnreadReminderCount()
    }
}