package com.shaz.shazcart.screens.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.screens.dashboard.DashboardActivity
import com.shaz.shazcart.screens.login.LoginActivity
import com.shaz.shazcart.screens.register.RegisterActivity

// FIX: WelcomeActivity now implements WelcomeContract.View and wires up WelcomePresenter,
// completing the MVP pattern that WelcomeContract and WelcomePresenter defined but never used.
class WelcomeActivity : AppCompatActivity(), WelcomeContract.View {

    private lateinit var presenter: WelcomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-login check stays in the Activity (not presenter) — it's a navigation concern
        // that needs the Android Context before the layout is even inflated
        val app = application as CustomApp
        if (app.isUserLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_welcome)

        // Wire up presenter with view (this) and model — matches MVP pattern in Login/Register/Dashboard
        presenter = WelcomePresenter(this, WelcomeModel())

        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            finish()
        }

        // Delegate button clicks to presenter — mirrors onGetStartedClicked/onLoginClicked in contract
        findViewById<Button>(R.id.buttonGetStarted).setOnClickListener {
            presenter.onGetStartedClicked()
        }

        findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    // WelcomeContract.View — presenter calls these, Activity handles navigation
    override fun showGetStarted() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun showLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
