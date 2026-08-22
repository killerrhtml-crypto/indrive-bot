package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.ui.admin.AdminDashboardActivity

class LoginPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val fadeInScale = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        btnLogin.startAnimation(fadeInScale)

        btnLogin.setOnClickListener {
            val pin = etPin.text.toString().trim()
            if (pin == "1234" || pin == "0000") {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
