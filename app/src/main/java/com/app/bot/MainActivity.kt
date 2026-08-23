package com.app.bot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.ui.dashboard.DashboardActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // Fallback por seguridad
        }
    }
}
