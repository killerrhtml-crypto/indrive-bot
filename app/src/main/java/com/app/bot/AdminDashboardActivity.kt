package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnAccessibility = findViewById<Button>(R.id.btnAccessibility)
        val btnStartBot = findViewById<Button>(R.id.btnStartBot)

        btnAccessibility?.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnStartBot?.setOnClickListener {
            Toast.makeText(this, "Bot Activado en segundo plano", Toast.LENGTH_SHORT).show()
        }
    }
}
