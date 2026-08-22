package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Espera 1.5 segundos y pasa directamente al Login de PIN
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginPinActivity::class.java))
            finish()
        }, 1500)
    }
}
