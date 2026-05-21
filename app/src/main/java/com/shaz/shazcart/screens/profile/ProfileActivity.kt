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
        findViewById<TextView>(R.id.textviewProfileMode).text = if (user.mode == "Solo") {
            "Solo account"
        } else {
            "Group account"
        }
    }

    override fun displayProfileSummary(summary: ProfileContract.ProfileSummary) {
        findViewById<TextView>(R.id.textviewProfileHero).text = summary.headline
        findViewById<TextView>(R.id.textviewStatsTitle).text = summary.modeLabel
        findViewById<TextView>(R.id.textviewStatsBody1).text = "${summary.primaryLabel}: ${summary.primaryValue}"
        findViewById<TextView>(R.id.textviewStatsBody2).text = "${summary.secondaryLabel}: ${summary.secondaryValue}"
        findViewById<TextView>(R.id.textviewStatsFooter).text = summary.note
    }
}