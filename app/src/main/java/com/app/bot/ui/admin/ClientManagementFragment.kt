package com.app.bot.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.bot.R

/**
 * Client Management - Gestión de clientes y sus aplicaciones
 */
class ClientManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_client_management, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clientListView = view.findViewById<ListView>(R.id.clientListView)
        val emptyMessage = view.findViewById<TextView>(R.id.tvEmptyClients)

        // Placeholder: No hay clientes aún
        emptyMessage?.text = "📋 No hay clientes registrados\n\nAgrega tu primer cliente desde el menú"
    }
}
