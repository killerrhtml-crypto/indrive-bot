package com.app.bot.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.R

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var updateManager: UpdateManager
    private lateinit var botController: BotController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvLog = findViewById<TextView>(R.id.tvLog)
        val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)
        val btnCheckUpdate = findViewById<ImageView>(R.id.btnCheckUpdate)

        // Inicializamos los módulos divididos
        updateManager = UpdateManager(this, tvLog)
        botController = BotController(tvStatus, tvLog, btnToggleBot)

        // Asignamos eventos de UI
        btnToggleBot.setOnClickListener {
            botController.toggle()
        }

        btnCheckUpdate.setOnClickListener {
            updateManager.checkForUpdates()
        }
    }
}
