package com.shaz.shazcart.screens.profile

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    override fun loadProfileDetails() {
        val user = model.getUser()
        view.displayUserInfo(user)

        // Route logic perfectly matches user's chosen mode
        if (user.mode == "Solo") {
            val (spent, limit) = model.getSoloStats()
            view.displaySoloBudget(spent, limit)
        } else {
            val (contributed, owed) = model.getGroupStats()
            view.displayGroupContributions(contributed, owed)
        }
    }
}