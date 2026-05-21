package com.shaz.shazcart.screens.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
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
        val radioGroupMode = findViewById<RadioGroup>(R.id.radioGroupMode)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            // Determine if Solo Mode is checked
            val isSolo = radioGroupMode.checkedRadioButtonId == R.id.radioSolo
            val selectedMode = if (isSolo) "Solo" else "Group"

            presenter.onRegisterClicked(
                nameField.text.toString(),
                emailField.text.toString(),
                passwordField.text.toString(),
                selectedMode
            )
        }
    }

    override fun showEmptyMessage() {
        toast("All fields are required")
    }

    override fun showSuccessMessage() {
        toast("Registration successful! You may now login.")
        finish()
    }

    override fun showErrorMessage() {
        toast("Registration failed")
    }
}