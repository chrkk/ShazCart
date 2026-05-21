package com.shaz.shazcart.screens.login

class LoginPresenter(
    private val view: LoginContract.View,
    private val model: LoginModel
) : LoginContract.Presenter {

    override fun validateCredentials(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showEmptyMessage()
        } else {
            if (model.isValidCredentials(username, password)) {
                model.setLoggedIn(true)
                // FIX: Do NOT call showSuccessMessage() and showDashboardScreen() together.
                // Calling two view methods back-to-back where one shows a Toast and the next
                // calls startActivity() + finish() creates a race condition — the Toast context
                // becomes invalid when the Activity finishes, causing a crash on second login.
                // Solution: navigate directly; show the success toast inside showDashboardScreen().
                view.showDashboardScreen()
            } else {
                view.showInvalidCredentials()
            }
        }
    }
}
