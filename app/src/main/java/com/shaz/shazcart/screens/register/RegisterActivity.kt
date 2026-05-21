package com.shaz.shazcart.screens.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.utils.toast

class RegisterActivity : AppCompatActivity(), RegisterContract.View {
    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        presenter = RegisterPresenter(this, RegisterModel(application as CustomApp))

        val nameField = findViewById<EditText>(R.id.edittextName)
        val emailField = findViewById<EditText>(R.id.edittextEmail)
        val passwordField = findViewById<EditText>(R.id.edittextPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            presenter.onRegisterClicked(
                nameField.text.toString(),
                emailField.text.toString(),
                passwordField.text.toString()
            )
        }
    }

    override fun showEmptyMessage() {
        toast("All fields are required")
    }

    override fun showSuccessMessage() {
        toast("Account created. Choose Solo or Group when you sign in.")
        finish()
    }

    override fun showErrorMessage() {
        toast("Registration failed")
    }
}