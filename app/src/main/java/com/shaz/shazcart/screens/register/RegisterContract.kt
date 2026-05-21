package com.shaz.shazcart.screens.register

interface RegisterContract {
    interface View {
        fun showEmptyMessage()
        fun showSuccessMessage()
        fun showErrorMessage()
    }
    interface Presenter {
        fun onRegisterClicked(name: String, email: String, password: String, mode: String)
    }
    interface Model {
        fun registerUser(name: String, email: String, password: String, mode: String): Boolean
    }
}