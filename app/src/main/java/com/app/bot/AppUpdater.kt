package com.app.bot

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class AppUpdater(private val context: Context) {
    private val updateUrl = "https://indrive-bot.onrender.com/update_info.json"
    private val channelId = "bot_updates"

    fun checkForUpdates(currentVersionCode: Int, showToast: Boolean = false) {
        thread {
            try {
                val response = URL(updateUrl).readText()
                val json = JSONObject(response)
                val latestVersion = json.getInt("versionCode")
                val apkUrl = json.getString("apkUrl")
                val releaseNotes = json.getString("releaseNotes")
                
                Handler(Looper.getMainLooper()).post {
                    if (latestVersion > currentVersionCode) {
                        showSystemNotification(apkUrl, releaseNotes)
                        if (showToast) Toast.makeText(context, "Actualización disponible. Revisa la barra de notificaciones.", Toast.LENGTH_LONG).show()
                    } else if (showToast) {
                        Toast.makeText(context, "Tu sistema King Pro está actualizado.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    if (showToast) Toast.makeText(context, "Buscando en servidor...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSystemNotification(apkUrl: String, notes: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Actualizaciones del Bot", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Configurar la acción al tocar la notificación (Descargar)
        val downloadIntent = Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl))
        val pendingIntent = PendingIntent.getActivity(context, 0, downloadIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done) // Icono nativo de sistema
            .setContentTitle("Nueva versión de King System Pro")
            .setContentText(notes)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
