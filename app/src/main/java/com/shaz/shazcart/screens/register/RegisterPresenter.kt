package com.shaz.shazcart.screens.register

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val model: RegisterContract.Model
) : RegisterContract.Presenter {

    override fun onRegisterClicked(name: String, email: String, password: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showEmptyMessage()
        } else {
            if (model.registerUser(name, email, password)) {
                view.showSuccessMessage()
            } else {
                view.showErrorMessage()
            }
        }
    }
}