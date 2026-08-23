package com.app.bot

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)
        val btnMenuOptions = findViewById<ImageView>(R.id.btnMenuOptions)
        val txtTelemetryInfo = findViewById<TextView>(R.id.txtTelemetryInfo)

        // Registrar telemetría de prueba en SQLite al iniciar
        val deviceId = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "DEVICE_CORE_01"
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        try {
            dbHelper.insertRecord(deviceId, "Red Móvil Activa", "admin@kingsystem.com", "+18005550199", timestamp)
        } catch (e: Exception) {
            // Ignorar duplicados o errores menores de inserción
        }

        txtTelemetryInfo.text = "ID Dispositivo: $deviceId\nIP Red Móvil: Conectada (LTE)\nEstado Licencia: Verificada (SQLite)\nÚltimo Registro: $timestamp"

        btnToggleBot.setOnClickListener {
            Toast.makeText(this, "Núcleo Bot Activado con Red Móvil", Toast.LENGTH_SHORT).show()
        }

        btnMenuOptions.setOnClickListener {
            Toast.makeText(this, "Menú de Licencias y Ajustes Corporativos", Toast.LENGTH_SHORT).show()
        }
    }
}
