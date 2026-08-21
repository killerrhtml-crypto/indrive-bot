package com.app.bot

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.config.ConfigurationManager
import com.app.bot.config.ProUserManager
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var configManager: ConfigurationManager
    private lateinit var proUserManager: ProUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Inicializar gestores de configuración
            configManager = ConfigurationManager(this)
            proUserManager = ProUserManager(this)

            val backendUrlInput = findViewById<TextInputEditText>(R.id.backendUrlInput)
            val connectButton = findViewById<Button>(R.id.connectButton)
            val serviceStatus = findViewById<TextView>(R.id.serviceStatus)
            val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

            // Cargar configuración local con protección
            val localConfig = try { configManager.getLocalConfig() } catch (e: Exception) { null }
            val savedUrl = preferences.getString(BACKEND_URL_KEY, DEFAULT_BACKEND_URL)

            if (backendUrlInput != null && savedUrl != null) {
                backendUrlInput.setText(savedUrl)
            }
            
            if (serviceStatus != null) {
                serviceStatus.text = getString(R.string.service_status, getServiceState())
            }

            // Debug: Mostrar información de configuración de forma segura
            val isProUser = try { proUserManager.isProUser() } catch (e: Exception) { false }
            Log.d("MainActivity", "🔍 Modo: ${if (isProUser) "PRO" else "BÁSICO"}")
            
            if (connectButton != null) {
                connectButton.setOnClickListener {
                    val url = backendUrlInput?.text?.toString()?.trim().orEmpty()
                    preferences.edit().putString(BACKEND_URL_KEY, url).apply()
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error crítico en onCreate", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            findViewById<TextView>(R.id.serviceStatus)?.text =
                getString(R.string.service_status, getServiceState())
        } catch (e: Exception) {
            Log.e("MainActivity", "Error en onResume", e)
        }
    }

    private fun getServiceState(): String {
        return try {
            val manager = getSystemService(AccessibilityManager::class.java)
            val enabledServices = manager?.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).orEmpty()
            if (enabledServices.any { it.resolveInfo.serviceInfo.packageName == packageName }) {
                getString(R.string.service_active)
            } else {
                getString(R.string.service_inactive)
            }
        } catch (e: Exception) {
            "Desconocido"
        }
    }

    companion object {
        const val BACKEND_URL_KEY = "backend_url"
        const val DEFAULT_BACKEND_URL = "wss://tu-servidor.localtunnel.me"
        const val PREFERENCES_NAME = "in_drive_bot_preferences"
    }
}
