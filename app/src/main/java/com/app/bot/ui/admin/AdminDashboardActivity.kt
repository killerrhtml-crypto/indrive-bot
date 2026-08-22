package com.app.bot.ui.admin

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bot.R
import com.app.bot.Driver
import com.app.bot.DriversAdapter
import com.app.bot.updater.AppUpdater

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var updater: AppUpdater
    private val CURRENT_VERSION_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        updater = AppUpdater(this)

        // Verificación silenciosa al entrar al panel
        updater.checkForUpdates(CURRENT_VERSION_CODE) { apkUrl ->
            Toast.makeText(this, "Nueva versión detectada. Iniciando instalación local...", Toast.LENGTH_LONG).show()
            updater.downloadAndInstall(apkUrl)
        }

        setupUI()
    }

    private fun setupUI() {
        val btnAccessibility = findViewById<Button>(R.id.btnAccessibility)
        val btnCheckUpdates = findViewById<Button>(R.id.btnCheckUpdates)
        val recyclerDrivers = findViewById<RecyclerView>(R.id.recyclerDrivers)

        btnAccessibility?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnCheckUpdates?.setOnClickListener {
            Toast.makeText(this, "Verificando actualizaciones...", Toast.LENGTH_SHORT).show()
            updater.checkForUpdates(CURRENT_VERSION_CODE) { apkUrl ->
                updater.downloadAndInstall(apkUrl)
            }
        }

        // Lista modular de conductores bajo supervisión
        val sampleDrivers = listOf(
            Driver("Carlos Mendoza", "carlos@driver.com", "Activo", "30/09/2026"),
            Driver("Ana Rodríguez", "ana@driver.com", "Pendiente", "15/09/2026")
        )

        recyclerDrivers?.layoutManager = LinearLayoutManager(this)
        recyclerDrivers?.adapter = DriversAdapter(sampleDrivers) { driver, _ ->
            Toast.makeText(this, "Administrando licencia de: ${driver.name}", Toast.LENGTH_SHORT).show()
        }
    }
}
