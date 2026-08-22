package com.app.bot.ui.admin

import android.graphics.Color
import android.widget.Button
import android.widget.TextView

class BotController(
    private val tvStatus: TextView,
    private val tvLog: TextView,
    private val btnToggleBot: Button
) {
    private var isRunning = false

    fun toggle(): Boolean {
        isRunning = !isRunning
        if (isRunning) {
            tvStatus.text = "Activo y Operando"
            tvStatus.setTextColor(Color.parseColor("#00E676"))
            btnToggleBot.text = "DETENER BOT"
            btnToggleBot.setBackgroundColor(Color.parseColor("#D32F2F"))
            tvLog.append("\n[Bot] Automatización iniciada correctamente en segundo plano.")
        } else {
            tvStatus.text = "Inactivo (Listo para iniciar)"
            tvStatus.setTextColor(Color.parseColor("#AAAAAA"))
            btnToggleBot.text = "INICIAR BOT"
            btnToggleBot.setBackgroundColor(Color.parseColor("#00E676"))
            tvLog.append("\n[Bot] Servicio detenido por el usuario.")
        }
        return isRunning
    }
}
