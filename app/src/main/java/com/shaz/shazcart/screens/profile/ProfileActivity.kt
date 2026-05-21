package com.shaz.shazcart.screens.profile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.User

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenter(this, ProfileModel(application as CustomApp))

        findViewById<Button>(R.id.buttonBackDashboard).setOnClickListener {
            finish() // Closes profile and returns to dashboard
        }

        presenter.loadProfileDetails()
    }

    override fun displayUserInfo(user: User) {
        findViewById<TextView>(R.id.textviewProfileName).text = user.username
        findViewById<TextView>(R.id.textviewProfileMode).text = "${user.mode} Account"
    }

    override fun displayGroupContributions(contributed: Double, owed: Double) {
        findViewById<TextView>(R.id.textviewStatsTitle).text = "Housemate Contributions"
        findViewById<TextView>(R.id.textviewStatsBody1).text = "Total Contributed: ₱$contributed"
        findViewById<TextView>(R.id.textviewStatsBody2).text = "Remaining Owed: ₱$owed"
    }

    override fun displaySoloBudget(spent: Double, budgetLimit: Double) {
        findViewById<TextView>(R.id.textviewStatsTitle).text = "Personal Budget Monitor"
        findViewById<TextView>(R.id.textviewStatsBody1).text = "Currently Spent: ₱$spent"

        val remaining = budgetLimit - spent
        findViewById<TextView>(R.id.textviewStatsBody2).text = "Budget Remaining: ₱$remaining / ₱$budgetLimit"
    }
}