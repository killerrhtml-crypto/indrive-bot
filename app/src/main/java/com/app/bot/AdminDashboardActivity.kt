package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var updater: AppUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        updater = AppUpdater(this)
        
        // Chequeo silencioso en segundo plano al abrir el panel
        updater.checkForUpdates(1, false)

        val btnAccessibility = findViewById<Button>(R.id.btnAccessibility)
        val btnStartBot = findViewById<Button>(R.id.btnStartBot)
        val btnCheckUpdates = findViewById<Button>(R.id.btnCheckUpdates)

        btnAccessibility?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnStartBot?.setOnClickListener {
            Toast.makeText(this, "Bot Activado en segundo plano", Toast.LENGTH_SHORT).show()
        }
        
        btnCheckUpdates?.setOnClickListener {
            Toast.makeText(this, "Conectando con Render...", Toast.LENGTH_SHORT).show()
            updater.checkForUpdates(1, true)
        }
    }
}
