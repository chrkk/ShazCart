package com.shaz.shazcart.utils

import android.app.Activity
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog

fun Activity.showModeSwitchDialog(currentMode: String, onModeSelected: (String) -> Unit) {
    val container = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(48, 24, 48, 0)
    }

    val radioGroup = RadioGroup(this).apply {
        orientation = RadioGroup.HORIZONTAL
    }

    val radioGroupBtn = RadioButton(this).apply {
        text = "Group"
        id = android.view.View.generateViewId()
    }
    val radioSoloBtn = RadioButton(this).apply {
        text = "Solo"
        id = android.view.View.generateViewId()
    }

    radioGroup.addView(radioGroupBtn)
    radioGroup.addView(radioSoloBtn)

    if (currentMode == "Solo") {
        radioSoloBtn.isChecked = true
    } else {
        radioGroupBtn.isChecked = true
    }

    container.addView(radioGroup)

    AlertDialog.Builder(this)
        .setTitle("Select mode")
        .setMessage("Choose Solo for personal use or Group for shared house management.")
        .setView(container)
        .setPositiveButton("Apply") { _, _ ->
            val selected = if (radioSoloBtn.isChecked) "Solo" else "Group"
            onModeSelected(selected)
        }
        .setNegativeButton("Cancel", null)
        .show()
}