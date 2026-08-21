package com.app.bot.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.bot.R
import com.app.bot.config.ProUserManager

/**
 * License Manager - Gestión de licencias PRO
 */
class LicenseManagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_license_manager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val proUserManager = ProUserManager(requireContext())
        val licenseData = proUserManager.getLicenseData()

        val tvLicenseInfo = view.findViewById<TextView>(R.id.tvLicenseInfo)
        val tvExpirationDate = view.findViewById<TextView>(R.id.tvExpirationDate)
        val btnRenewLicense = view.findViewById<Button>(R.id.btnRenewLicense)

        if (licenseData != null) {
            val licenseKey = licenseData.optString("license_key", "N/A")
            val expiration = licenseData.optString("expiration", "No especificada")

            tvLicenseInfo?.text = "🔑 Licencia: $licenseKey"
            tvExpirationDate?.text = "📅 Vencimiento: $expiration"
        } else {
            tvLicenseInfo?.text = "❌ No hay licencia activa"
        }

        btnRenewLicense?.setOnClickListener {
            // TODO: Implementar lógica de renovación
            tvLicenseInfo?.text = "🔄 Renovación en progreso..."
        }
    }
}
