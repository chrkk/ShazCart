package com.shaz.shazcart.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.shaz.shazcart.R

class DashboardListAdapter<T>(
    private val context: Context,
    private val itemList: MutableList<T>,
    private val getPrimary: (T) -> String,
    private val getSecondary: (T) -> String,
    private val onClick: (T) -> Unit,
) : BaseAdapter() {

    override fun getCount(): Int = itemList.size

    override fun getItem(position: Int): Any = itemList[position] as Any

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_dashboard_row, parent, false
        )


        val icon = view.findViewById<ImageView>(R.id.imageview_row_icon)
        val primaryText = view.findViewById<TextView>(R.id.textview_row_primary)
        val secondaryText = view.findViewById<TextView>(R.id.textview_row_secondary)


        val item = itemList[position]
        primaryText.text = getPrimary(item)
        secondaryText.text = getSecondary(item)


        view.setOnClickListener {
            onClick(item)
        }

        return view
    }
}
