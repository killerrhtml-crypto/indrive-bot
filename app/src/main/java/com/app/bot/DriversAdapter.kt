package com.app.bot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Driver(val name: String, val email: String, val status: String, val expiry: String)

class DriversAdapter(
    private val drivers: List<Driver>,
    private val onActionClick: (Driver, String) -> Unit
) : RecyclerView.Adapter<DriversAdapter.DriverViewHolder>() {

    class DriverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val driverName: TextView = view.findViewById(android.R.id.text1)
        val driverEmail: TextView = view.findViewById(android.R.id.text2)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        // Usamos un layout nativo simple para el listado del operador
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = drivers[position]
        holder.driverName.text = driver.name
        holder.driverEmail.text = "${driver.email} | Estado: ${driver.status}"
    }

    override fun getItemCount() = drivers.size
}
