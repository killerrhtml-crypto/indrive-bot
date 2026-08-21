package com.app.bot.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.bot.R

/**
 * Security Settings - Configuración de seguridad
 */
class SecuritySettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_security_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swTwoFactor = view.findViewById<Switch>(R.id.swTwoFactor)
        val swEncryption = view.findViewById<Switch>(R.id.swEncryption)
        val tvSecurityStatus = view.findViewById<TextView>(R.id.tvSecurityStatus)

        swTwoFactor?.isChecked = true
        swEncryption?.isChecked = true

        tvSecurityStatus?.text = "✅ Sistema de seguridad activo"
    }
}
