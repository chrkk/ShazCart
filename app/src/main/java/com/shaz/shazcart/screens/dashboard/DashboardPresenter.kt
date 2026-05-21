package com.shaz.shazcart.screens.dashboard

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        val (totalItems, pendingItems, totalSpent) = model.getSummary()
        view.showSummary(totalItems, pendingItems, totalSpent)

        val housemates = model.getHousematesStatus()
        view.showHousematesStatus(housemates)

        val sharedList = model.getSharedList()
        view.showSharedList(sharedList)
    }
}
