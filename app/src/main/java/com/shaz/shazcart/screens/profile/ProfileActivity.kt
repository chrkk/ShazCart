package com.shaz.shazcart.screens.profile

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.utils.toast
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

        findViewById<Button>(R.id.buttonSwitchMode).setOnClickListener {
            showModeSwitchDialog()
        }

        presenter.loadProfileDetails()
    }

    override fun displayUserInfo(user: User) {
        findViewById<TextView>(R.id.textviewProfileName).text = user.displayName
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

    private fun showModeSwitchDialog() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val radioGroup = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
        }

        val radioGroupBtn = RadioButton(this).apply { text = "Group"; id = android.view.View.generateViewId() }
        val radioSoloBtn = RadioButton(this).apply { text = "Solo"; id = android.view.View.generateViewId() }
        radioGroup.addView(radioGroupBtn)
        radioGroup.addView(radioSoloBtn)

        val currentMode = (application as CustomApp).getUser().mode
        if (currentMode == "Solo") radioSoloBtn.isChecked = true else radioGroupBtn.isChecked = true

        container.addView(radioGroup)

        AlertDialog.Builder(this)
            .setTitle("Select mode")
            .setMessage("Choose Solo for personal use or Group for shared house management.")
            .setView(container)
            .setPositiveButton("Apply") { _, _ ->
                val selected = if (radioSoloBtn.isChecked) "Solo" else "Group"
                (application as CustomApp).updateUserMode(selected)
                presenter.loadProfileDetails()
                toast("Mode switched to $selected")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}