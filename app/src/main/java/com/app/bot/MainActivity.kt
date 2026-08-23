package com.app.bot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private val networkManager = NetworkManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)
        val btnMenuOptions = findViewById<ImageView>(R.id.btnMenuOptions)
        val txtTelemetryInfo = findViewById<TextView>(R.id.txtTelemetryInfo)
        
        val deviceId = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "DEVICE_01"
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // UI Feedback en lugar de Toasts
        btnToggleBot.setOnClickListener {
            btnToggleBot.setBackgroundColor(Color.parseColor("#10B981")) // Verde Activo
            btnToggleBot.text = "NÚCLEO EN LÍNEA"
            
            // Verificación asíncrona real del servidor
            CoroutineScope(Dispatchers.Main).launch {
                val (success, message) = networkManager.checkServerUpdate("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/update_info.json")
                txtTelemetryInfo.text = "ID: $deviceId\nRed Móvil: Activa\nEstado Servidor: $message\nÚltimo: $timestamp"
            }
        }

        btnMenuOptions.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
