package com.shaz.shazcart.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.utils.getEditTextValue
import com.shaz.shazcart.utils.toast
import com.shaz.shazcart.screens.dashboard.DashboardActivity
import com.shaz.shazcart.screens.register.RegisterActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this, LoginModel(application as CustomApp))

        findViewById<Button>(R.id.buttonSignIn).setOnClickListener {
            val email: String = getEditTextValue(R.id.edittextEmail)
            val password: String = getEditTextValue(R.id.edittextPassword)
            val radioGroupMode = findViewById<RadioGroup>(R.id.radioGroupMode)
            val isSolo = radioGroupMode.checkedRadioButtonId == R.id.radioSolo
            val selectedMode = if (isSolo) "Solo" else "Group"
            presenter.validateCredentials(email, password, selectedMode)
        }

        findViewById<TextView>(R.id.textviewSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun showEmptyMessage() {
        toast("Fields cannot be empty")
    }

    override fun showSuccessMessage() {
        toast("Login successful")
    }

    override fun showDashboardScreen() {
        // FIX: Show the success toast here — AFTER the navigation decision is made,
        // and BEFORE startActivity so the toast fires while this Activity is still alive.
        // Then finish() this Activity so the back stack is clean.
        toast("Login successful")
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showInvalidCredentials() {
        toast("Invalid credentials")
    }
}
