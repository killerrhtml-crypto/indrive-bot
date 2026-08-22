package com.app.bot.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.app.bot.R
import com.app.bot.updater.AppUpdater

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appUpdater: AppUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        drawerLayout = findViewById(R.id.drawerLayout)
        appUpdater = AppUpdater(this)

        // Botón de la nube para verificación OTA directa con GitHub
        val cloudContainer = findViewById<ImageView>(R.id.cloudContainer)
        cloudContainer?.setOnClickListener {
            Toast.makeText(this, "Buscando actualizaciones...", Toast.LENGTH_SHORT).show()
            appUpdater.checkForUpdatesManual()
        }

        // Botón de inicio del bot autónomo
        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        
        btnConnect?.setOnClickListener {
            tvStatus.text = "Estado: Bot Activo y Operando"
            Toast.makeText(this, "Automatización iniciada correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}
