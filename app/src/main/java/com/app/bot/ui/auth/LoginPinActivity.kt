package com.app.bot.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.R
import com.app.bot.ui.dashboard.DashboardActivity

class LoginPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val pin = etPin.text.toString().trim()
            // PIN por defecto configurable ("1234" o el que prefieras)
            if (pin == "1234") {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
