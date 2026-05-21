package com.shaz.shazcart.screens.register

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val model: RegisterContract.Model
) : RegisterContract.Presenter {

    override fun onRegisterClicked(name: String, email: String, password: String, mode: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showEmptyMessage()
        } else {
            if (model.registerUser(name, email, password, mode)) {
                view.showSuccessMessage()
            } else {
                view.showErrorMessage()
            }
        }
    }
}