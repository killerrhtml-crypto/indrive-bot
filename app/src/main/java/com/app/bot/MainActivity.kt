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

        // Inicializar gestores de configuración
        configManager = ConfigurationManager(this)
        proUserManager = ProUserManager(this)

        val backendUrlInput = findViewById<TextInputEditText>(R.id.backendUrlInput)
        val connectButton = findViewById<Button>(R.id.connectButton)
        val serviceStatus = findViewById<TextView>(R.id.serviceStatus)
        val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        // Cargar configuración local
        val localConfig = configManager.getLocalConfig()
        val savedUrl = preferences.getString(BACKEND_URL_KEY, DEFAULT_BACKEND_URL)
        
        backendUrlInput.setText(savedUrl)
        serviceStatus.text = getString(R.string.service_status, getServiceState())

        // Debug: Mostrar información de configuración
        val isProUser = proUserManager.isProUser()
        Log.d("MainActivity", "🔍 Modo: ${if (isProUser) "PRO" else "BÁSICO"}")
        Log.d("MainActivity", "📋 Config Version: ${localConfig.optString("version")}")
        Log.d("MainActivity", "📍 Admin ID: ${proUserManager.getAdminId()}")

        connectButton.setOnClickListener {
            val url = backendUrlInput.text?.toString()?.trim().orEmpty()
            preferences.edit().putString(BACKEND_URL_KEY, url).apply()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.serviceStatus)?.text =
            getString(R.string.service_status, getServiceState())
    }

    private fun getServiceState(): String {
        val manager = getSystemService(AccessibilityManager::class.java)
        val enabledServices = manager?.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ).orEmpty()
        return if (enabledServices.any { it.resolveInfo.serviceInfo.packageName == packageName }) {
            getString(R.string.service_active)
        } else {
            getString(R.string.service_inactive)
        }
    }

    companion object {
        const val BACKEND_URL_KEY = "backend_url"
        const val DEFAULT_BACKEND_URL = "wss://tu-servidor.localtunnel.me"
        const val PREFERENCES_NAME = "in_drive_bot_preferences"
    }
}
