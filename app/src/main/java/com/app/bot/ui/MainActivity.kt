package com.app.bot.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.R
import com.app.bot.config.ConfigurationManager
import com.app.bot.config.ProUserManager
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceStatus = findViewById<TextView>(R.id.serviceStatus)
        val backendUrlInput = findViewById<TextInputEditText>(R.id.backendUrlInput)
        val connectButton = findViewById<Button>(R.id.connectButton)

        try {
            // Inicialización controlada
            val configManager = ConfigurationManager(this)
            val proUserManager = ProUserManager(this)
            val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

            val savedUrl = preferences.getString(BACKEND_URL_KEY, DEFAULT_BACKEND_URL)
            backendUrlInput?.setText(savedUrl)
            serviceStatus?.text = "Estado: Todo OK al iniciar"

            connectButton?.setOnClickListener {
                val url = backendUrlInput?.text?.toString()?.trim().orEmpty()
                preferences.edit().putString(BACKEND_URL_KEY, url).apply()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }

        } catch (e: Throwable) {
            // Si ocurre CUALQUIER error, lo atrapamos y lo mostramos en pantalla para leerlo
            serviceStatus?.text = "CRASH: ${e.localizedMessage ?: e.javaClass.simpleName}"
            serviceStatus?.textSize = 14f
        }
    }

    companion object {
        const val BACKEND_URL_KEY = "backend_url"
        const val DEFAULT_BACKEND_URL = "wss://tu-servidor.localtunnel.me"
        const val PREFERENCES_NAME = "in_drive_bot_preferences"
    }
}
