package com.shaz.shazcart.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.shaz.shazcart.R
import com.shaz.shazcart.app.CustomApp
import com.shaz.shazcart.data.GroceryItem
import com.shaz.shazcart.data.Housemate
import com.shaz.shazcart.helper.DashboardListAdapter
import com.shaz.shazcart.screens.welcome.WelcomeActivity

class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var presenter: DashboardPresenter

    private val housemateList = mutableListOf<Housemate>()
    private val groceryList = mutableListOf<GroceryItem>()

    private lateinit var housematesAdapter: DashboardListAdapter<Housemate>
    private lateinit var groceryAdapter: DashboardListAdapter<GroceryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val housematesListView = findViewById<ListView>(R.id.listviewHousemates)
        val groceryListView = findViewById<ListView>(R.id.listviewGrocery)


        housematesAdapter = DashboardListAdapter(
            context = this,
            itemList = housemateList,
            getPrimary = { housemate -> housemate.name },
            getSecondary = { housemate -> housemate.status },
            onClick = { housemate ->
                Toast.makeText(this, "${housemate.name}: ${housemate.status}", Toast.LENGTH_SHORT).show()
            }

        )

        groceryAdapter = DashboardListAdapter(
            context = this,
            itemList = groceryList,
            getPrimary = { item -> item.itemName },
            getSecondary = { item -> "${item.assignedTo} — ${item.price}" },
            onClick = { item ->
                Toast.makeText(this, "${item.itemName} bought by ${item.assignedTo} for ${item.price}", Toast.LENGTH_SHORT).show()
            }

        )

        housematesListView.adapter = housematesAdapter
        groceryListView.adapter = groceryAdapter

        findViewById<Button>(R.id.buttonAddHousemate).setOnClickListener {
            housemateList.add(Housemate("New Housemate", "Owes ₱0 ⚠️"))
            housematesAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Housemate has been added.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.buttonAddGrocery).setOnClickListener {
            groceryList.add(GroceryItem("New Item", "Unassigned", "₱0"))
            groceryAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Grocery item has been added.", Toast.LENGTH_SHORT).show()
        }

        housematesListView.setOnItemClickListener { _, _, position, _ ->
            showRemoveHousemateDialog(position)
        }

        groceryListView.setOnItemClickListener { _, _, position, _ ->
            showRemoveGroceryDialog(position)
        }

        findViewById<Button>(R.id.buttonLogout).setOnClickListener {
            (application as CustomApp).clearUser()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        presenter = DashboardPresenter(this, DashboardModel(application as CustomApp))
        presenter.loadDashboard()
    }

    private fun showRemoveHousemateDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Housemate")
        builder.setMessage("Are you sure you want to remove ${housemateList[position].name}?")

        builder.setPositiveButton("Remove") { _, _ ->
            val removed = housemateList.removeAt(position)
            housematesAdapter.notifyDataSetChanged()
            Toast.makeText(this, "${removed.name} has been removed.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showRemoveGroceryDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Item")
        builder.setMessage("Are you sure you want to remove ${groceryList[position].itemName}?")

        builder.setPositiveButton("Remove") { _, _ ->
            val removed = groceryList.removeAt(position)
            groceryAdapter.notifyDataSetChanged()
            Toast.makeText(this, "${removed.itemName} has been removed.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // --- DashboardContract.View implementations ---

    override fun showSummary(totalItems: Int, pendingItems: Int, totalSpent: Double) {
        findViewById<TextView>(R.id.textviewTotalItems).text = "$totalItems Total Items"
        findViewById<TextView>(R.id.textviewPendingItems).text = "$pendingItems Pending"
        findViewById<TextView>(R.id.textviewTotalSpent).text = "₱$totalSpent Total Spent"
    }

    override fun showHousematesStatus(housemates: MutableList<Housemate>) {
        housemateList.clear()
        housemateList.addAll(housemates)
        housematesAdapter.notifyDataSetChanged()
    }

    override fun showSharedList(items: MutableList<GroceryItem>) {
        groceryList.clear()
        groceryList.addAll(items)
        groceryAdapter.notifyDataSetChanged()
    }
}
