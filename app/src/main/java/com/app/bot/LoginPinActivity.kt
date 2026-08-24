package com.killerrhtml.indrivebot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginPinActivity : AppCompatActivity() {

    private lateinit var pinInput: TextInputEditText
    private lateinit var pinInputLayout: TextInputLayout
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_pin)

        pinInput = findViewById(R.id.etPin)
        pinInputLayout = findViewById(R.id.pinInputLayout)
        loginButton = findViewById(R.id.btnLogin)

        loginButton.setOnClickListener { validatePin() }
    }

    private fun validatePin() {
        val pin = pinInput.text?.toString().orEmpty()
        pinInputLayout.error = null

        if (pin.length !in MIN_PIN_LENGTH..MAX_PIN_LENGTH) {
            pinInputLayout.error = getString(R.string.admin_pin_length_error)
            return
        }

        loginButton.isEnabled = false
        if (isDemoPinValid(pin)) {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        } else {
            pinInputLayout.error = getString(R.string.admin_pin_invalid_error)
            pinInput.text?.clear()
            loginButton.isEnabled = true
        }
    }

    private fun isDemoPinValid(pin: String): Boolean = pin in DEMO_PINS

    companion object {
        private const val MIN_PIN_LENGTH = 4
        private const val MAX_PIN_LENGTH = 6
        private val DEMO_PINS = setOf("1234", "8888")
    }
}