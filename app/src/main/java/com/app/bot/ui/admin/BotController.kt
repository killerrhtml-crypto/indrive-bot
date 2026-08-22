package com.app.bot.ui.admin

import android.content.Context
import android.graphics.Color
import android.widget.Button
import android.widget.TextView

class BotController(
    private val context: Context,
    private val tvStatus: TextView,
    private val tvLog: TextView,
    private val btnToggleBot: Button
) {
    private var isRunning = false
    private val notificationHelper = NotificationHelper(context)

    fun toggle(): Boolean {
        isRunning = !isRunning
        if (isRunning) {
            tvStatus.text = "Activo y Operando"
            tvStatus.setTextColor(Color.parseColor("#00E676"))
            btnToggleBot.text = "DETENER BOT"
            btnToggleBot.setBackgroundColor(Color.parseColor("#D32F2F"))
            tvLog.append("\n[Bot] Automatización iniciada correctamente.")
            notificationHelper.showNotification("InDrive Bot", "El bot se ha iniciado y está operando.", 101)
        } else {
            tvStatus.text = "Inactivo (Listo para iniciar)"
            tvStatus.setTextColor(Color.parseColor("#AAAAAA"))
            btnToggleBot.text = "INICIAR BOT"
            btnToggleBot.setBackgroundColor(Color.parseColor("#00E676"))
            tvLog.append("\n[Bot] Servicio detenido por el usuario.")
            notificationHelper.showNotification("InDrive Bot", "El servicio del bot ha sido detenido.", 102)
        }
        return isRunning
    }
}
