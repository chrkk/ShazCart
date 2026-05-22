package com.shaz.shazcart.screens.dashboard

import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Settlement
import com.shaz.shazcart.data.Housemate

class DashboardModel(private val app: CustomApp) {

    private val housemates = mutableListOf<Housemate>()

    private val groceryList = mutableListOf<GroceryItem>()
    private val settlements = mutableListOf<Settlement>()

    private fun parsePrice(price: String): Double {
        return price.replace("₱", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
    }

    fun getCurrentUserName(): String {
        return app.getUser().displayName
    }

    fun ensureCurrentUserIncluded() {
        if (getMode() != "Group") {
            return
        }

        val currentUserName = getCurrentUserName()
        if (housemates.none { it.name == currentUserName }) {
            housemates.add(0, Housemate(currentUserName))
        }
    }

    fun isCurrentUserHousemate(name: String): Boolean {
        return name == getCurrentUserName()
    }

    fun getSummary(): Triple<Int, Int, Double> {
        val totalItems = groceryList.size
        val pendingItems = groceryList.count { it.assignedTo == "Unassigned" }

        var totalSpent = 0.0
        for (item in groceryList) {
            totalSpent += parsePrice(item.price)
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
            val price = parsePrice(item.price)
            val currentPaid = paidAmounts[item.assignedTo] ?: 0.0
            paidAmounts[item.assignedTo] = currentPaid + price
        }

        // 2. Compare what they should pay (splitAmount) vs what they already paid or received
        for (housemate in housemates) {
            val paid = paidAmounts[housemate.name] ?: 0.0
            val balance = splitAmount - paid - housemate.settlementPaid + housemate.settlementReceived
            housemate.netBalance = balance

            if (balance > 0.01) {
                housemate.amountOwed = balance
                housemate.status = "Needs to pay ₱${String.format("%.2f", balance)} 🔴"
            } else if (balance < -0.01) {
                housemate.amountOwed = 0.0
                housemate.status = "Should receive ₱${String.format("%.2f", -balance)} 🟢"
            } else {
                housemate.amountOwed = 0.0
                housemate.status = "Settled ✅"
            }
        }
    }

    fun addSettlement(from: String, to: String, amount: Double) {
        settlements.add(Settlement(from, to, amount))

        housemates.find { it.name == from }?.let { housemate ->
            housemate.settlementPaid += amount
        }
        housemates.find { it.name == to }?.let { housemate ->
            housemate.settlementReceived += amount
        }

        updateExpenseSplit()
    }

    fun getSettlements(): List<Settlement> = settlements

    fun removeSettlement(index: Int): Settlement = settlements.removeAt(index)

    fun getHousematesStatus(): List<Housemate> {
        updateExpenseSplit() // Recalculate dependencies before returning
        return housemates
    }

    fun getSharedList(): List<GroceryItem> = groceryList

    fun getSettlementSummary(): Pair<String, String> {
        updateExpenseSplit()

        val payers = housemates
            .filter { it.netBalance > 0.01 }
            .sortedByDescending { it.netBalance }

        val receivers = housemates
            .filter { it.netBalance < -0.01 }
            .sortedByDescending { kotlin.math.abs(it.netBalance) }

        val needsToPaySummary = if (payers.isEmpty()) {
            "Everyone is settled."
        } else {
            payers.joinToString("\n") { housemate ->
                "• ${housemate.name} · ₱${String.format("%.2f", housemate.netBalance)}"
            }
        }

        val shouldReceiveSummary = if (receivers.isEmpty()) {
            "Nobody is owed money."
        } else {
            receivers.joinToString("\n") { housemate ->
                "• ${housemate.name} · ₱${String.format("%.2f", kotlin.math.abs(housemate.netBalance))}"
            }
        }

        return needsToPaySummary to shouldReceiveSummary
    }

    fun getSettlementEntries(): Pair<List<DashboardContract.SettlementEntry>, List<DashboardContract.SettlementEntry>> {
        updateExpenseSplit()

        val payers = housemates.mapIndexedNotNull { index, housemate ->
            if (housemate.netBalance > 0.01) {
                DashboardContract.SettlementEntry(housemate.name, housemate.netBalance, true, index)
            } else {
                null
            }
        }.sortedByDescending { it.amount }

        val receivers = housemates.mapIndexedNotNull { index, housemate ->
            if (housemate.netBalance < -0.01) {
                DashboardContract.SettlementEntry(housemate.name, kotlin.math.abs(housemate.netBalance), false, index)
            } else {
                null
            }
        }.sortedByDescending { it.amount }

        return payers to receivers
    }

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

    fun setHousematePayment(position: Int, amount: Double): Housemate {
        housemates[position].settlementPaid = amount
        updateExpenseSplit()
        return housemates[position]
    }

    fun settleHousemate(position: Int): Housemate {
        updateExpenseSplit()
        val housemate = housemates[position]
        if (housemate.netBalance > 0.01) {
            housemate.settlementPaid += housemate.amountOwed
        } else if (housemate.netBalance < -0.01) {
            housemate.settlementReceived += kotlin.math.abs(housemate.netBalance)
        }
        updateExpenseSplit()
        return housemate
    }

    fun clearHousematePayment(position: Int): Housemate {
        housemates[position].settlementPaid = 0.0
        housemates[position].settlementReceived = 0.0
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