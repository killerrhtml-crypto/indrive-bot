package com.app.bot.ui.admin

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val cloudContainer = findViewById<FrameLayout>(R.id.cloudContainer)
        val ivCloudStatus = findViewById<ImageView>(R.id.ivCloudStatus)
        val btnAccessibility = findViewById<Button>(R.id.btnAccessibilityAction)
        val recyclerRequests = findViewById<RecyclerView>(R.id.recyclerRequests)

        // Abrir menú lateral
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Variable para almacenar la URL de descarga detectada
        var pendingApkUrl: String? = null

        // Chequeo inicial en segundo plano
        updater.checkForUpdates(CURRENT_VERSION_CODE) { apkUrl ->
            pendingApkUrl = apkUrl
            ivCloudStatus.setColorFilter(0xFFFF5252.toInt()) // Rojo = Actualización lista
            Toast.makeText(this, "¡Nueva versión disponible en la nube!", Toast.LENGTH_SHORT).show()
        }

        // Único listener optimizado para el contenedor de la nube (Paso 1 resuelto)
        cloudContainer.setOnClickListener {
            val url = pendingApkUrl
            if (url != null) {
                updater.downloadAndInstall(url)
            } else {
                Toast.makeText(this, "El sistema ya se encuentra actualizado (Al día)", Toast.LENGTH_SHORT).show()
            }
        }

        btnAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // Carga de datos de prueba para la tabla de gestión de usuarios/licencias
        val sampleRequests = listOf(
            Driver("Carlos Mendoza", "carlos@driver.com", "Solicita Licencia", "Pendiente"),
            Driver("Ana Rodríguez", "ana@driver.com", "Servicio Activo", "Aprobado"),
            Driver("José Pérez", "jose@driver.com", "Renovación", "Pendiente")
        )

        recyclerRequests.layoutManager = LinearLayoutManager(this)
        recyclerRequests.adapter = DriversAdapter(sampleRequests) { driver, _ ->
            Toast.makeText(this, "Gestionando usuario: ${driver.name}", Toast.LENGTH_SHORT).show()
        }
    }
}
