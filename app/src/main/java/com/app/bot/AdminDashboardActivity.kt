package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var updater: AppUpdater
    private val CURRENT_VERSION_CODE = 1 // Sube este número localmente cuando hagas mejoras grandes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        updater = AppUpdater(this)
        
        // 1. Chequeo automático en cuanto abres la app (Silencioso)
        updater.checkForUpdates(CURRENT_VERSION_CODE, false)

        val btnCheckUpdates = findViewById<Button>(R.id.btnCheckUpdates)
        
        // 2. Chequeo manual tocando el botón de la nube/actualizar
        btnCheckUpdates?.setOnClickListener {
            Toast.makeText(this, "Conectando con la nube...", Toast.LENGTH_SHORT).show()
            updater.checkForUpdates(CURRENT_VERSION_CODE, true)
        }
    }
}
