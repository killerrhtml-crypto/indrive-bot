package com.app.bot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DriversAdapter(
    private val drivers: List<Driver>,
    private val onActionClick: (Driver, String) -> Unit
) : RecyclerView.Adapter<DriversAdapter.DriverViewHolder>() {

    class DriverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDriverName)
        val tvDetails: TextView = view.findViewById(R.id.tvDriverDetails)
        val btnAction: Button = view.findViewById(R.id.btnDriverAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = drivers[position]
        holder.tvName.text = driver.name
        holder.tvDetails.text = "${driver.email} | Licencia: ${driver.expiry}"
        holder.btnAction.text = driver.status

        holder.btnAction.setOnClickListener {
            onActionClick(driver, driver.status)
        }
    }

    override fun getItemCount() = drivers.size
}
