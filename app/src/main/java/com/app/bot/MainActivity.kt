package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.ui.dashboard.DashboardActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            val tvStatus = findViewById<TextView>(R.id.tvStatus)
            val btnConnect = findViewById<Button>(R.id.btnConnect)

            tvStatus?.text = "Estado: Núcleo listo y estable"

            btnConnect?.setOnClickListener {
                try {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir Dashboard: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Opcional: Auto-redirección rápida o mantener en pantalla de bienvenida
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    // Evitar cualquier crash silencioso
                }
            }, 1500)

        } catch (e: Exception) {
            Toast.makeText(this, "Error crítico en inicio: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
