package com.killerrhtml.indrivebot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date

class DriversAdapter(
    private val drivers: MutableList<Driver>,
    private val onDateRequested: (Int) -> Unit,
    private val onStatusChanged: (Int, DriverStatus) -> Unit
) : RecyclerView.Adapter<DriversAdapter.DriverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_card, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.bind(drivers[position], position)
    }

    override fun getItemCount(): Int = drivers.size

    fun updateStatus(position: Int, status: DriverStatus) {
        drivers[position].status = status
        notifyItemChanged(position)
    }

    fun updateExpiry(position: Int, expiry: Long) {
        drivers[position].licenseExpiry = expiry
        notifyItemChanged(position)
    }

    inner class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.driverName)
        private val email: TextView = itemView.findViewById(R.id.driverEmail)
        private val status: TextView = itemView.findViewById(R.id.driverStatus)
        private val expiry: TextView = itemView.findViewById(R.id.driverExpiry)
        private val dateButton: Button = itemView.findViewById(R.id.btnLicenseDate)
        private val acceptButton: Button = itemView.findViewById(R.id.btnAccept)
        private val rejectButton: Button = itemView.findViewById(R.id.btnReject)

        fun bind(driver: Driver, position: Int) {
            name.text = driver.name
            email.text = driver.email
            status.text = itemView.context.getString(
                R.string.driver_status,
                driver.status.name
            )
            expiry.text = driver.licenseExpiry?.let {
                itemView.context.getString(
                    R.string.driver_expiry,
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(it))
                )
            } ?: itemView.context.getString(R.string.driver_expiry_unassigned)
            dateButton.setOnClickListener { onDateRequested(position) }
            acceptButton.setOnClickListener { onStatusChanged(position, DriverStatus.ACTIVE) }
            rejectButton.setOnClickListener { onStatusChanged(position, DriverStatus.REJECTED) }
        }
    }
}