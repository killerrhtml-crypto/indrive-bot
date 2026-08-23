package com.app.bot

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val listView = ListView(this)
        val options = arrayOf(
            "🎨 Cambiar Tema (Oscuro / Blanco)",
            "🔤 Ajustar Fuentes del Sistema",
            "🛡️ Seguridad Biométrica (Huella Dactilar)",
            "📋 Ver Registro de Licencias y Telemetría SQL",
            "🌐 Estado de Conectividad (Red Móvil LTE)"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> Toast.makeText(this, "Alternando Tema Corporativo...", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(this, "Configurador de Tipografías", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(this, "Configurando Biometría por Huella...", Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(this, "Abriendo registros de base de datos SQLite...", Toast.LENGTH_SHORT).show()
                4 -> Toast.makeText(this, "Red móvil operando de forma estable", Toast.LENGTH_SHORT).show()
            }
        }
        
        setContentView(listView)
        title = "King System - Configuración"
    }
}
