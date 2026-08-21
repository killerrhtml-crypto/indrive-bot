package com.app.bot.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.bot.R
import com.app.bot.config.ProUserManager

/**
 * Admin Dashboard - Vista general del sistema para administradores
 */
class AdminDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val proUserManager = ProUserManager(requireContext())
        val adminId = proUserManager.getAdminId()

        view.findViewById<TextView>(R.id.tvAdminWelcome)?.text =
            "Bienvenido, $adminId!\n\n📊 Panel de Control Principal"
    }
}
