package com.killerrhtml.indrivebot.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.killerrhtml.indrivebot.R
import com.killerrhtml.indrivebot.engine.BotEngine

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var botEngine: BotEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        botEngine = BotEngine()

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)

        btnToggleBot.setOnClickListener {
            val isActive = botEngine.toggleState()
            if (isActive) {
                tvStatus.text = "Motor Activo y Escaneando"
                tvStatus.setTextColor(Color.parseColor("#00E676"))
                btnToggleBot.text = "Detener Automatización"
                Toast.makeText(this, "Automatización iniciada", Toast.LENGTH_SHORT).show()
            } else {
                tvStatus.text = "Motor Pausado"
                tvStatus.setTextColor(Color.parseColor("#FFA726"))
                btnToggleBot.text = "Iniciar Automatización"
                Toast.makeText(this, "Automatización detenida", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
