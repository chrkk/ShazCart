package com.shaz.shazcart.screens.profile

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    override fun loadProfileDetails() {
        val user = model.getUser()
        view.displayUserInfo(user)
        view.displayProfileSummary(model.getProfileSummary())
    }
}