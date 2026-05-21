package com.shaz.shazcart.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shaz.shazcart.R

class DashboardRecyclerAdapter<T>(
    private val getPrimary: (T) -> String,
    private val getSecondary: (T) -> String,
    private val onClick: (T, Int) -> Unit,
    private val onLongClick: (T, Int) -> Unit
) : RecyclerView.Adapter<DashboardRecyclerAdapter<T>.ViewHolder>() {

    private val itemList = mutableListOf<T>()

    fun submitList(list: List<T>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T? {
        return itemList.getOrNull(position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imageview_row_icon)
        val primaryText: TextView = view.findViewById(R.id.textview_row_primary)
        val secondaryText: TextView = view.findViewById(R.id.textview_row_secondary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_dashboard_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.primaryText.text = getPrimary(item)
        holder.secondaryText.text = getSecondary(item)

        holder.itemView.setOnClickListener {
            onClick(item, position)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(item, position)
            true
        }
    }

    override fun getItemCount(): Int = itemList.size
}