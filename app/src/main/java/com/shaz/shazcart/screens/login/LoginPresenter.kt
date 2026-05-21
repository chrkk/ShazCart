package com.shaz.shazcart.screens.login

class LoginPresenter(
    private val view: LoginContract.View,
    private val model: LoginModel
) : LoginContract.Presenter {

    override fun validateCredentials(email: String, password: String, mode: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showEmptyMessage()
        } else {
            if (model.isValidCredentials(email, password)) {
                model.setActiveMode(mode)
                model.setLoggedIn(true)
                view.showDashboardScreen()
            } else {
                view.showInvalidCredentials()
            }
        }
    }
}
