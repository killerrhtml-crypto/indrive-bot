package com.app.bot

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class AppUpdater(private val context: Context) {
    // URL estática anclada a tu servidor Render
    private val updateUrl = "https://indrive-bot.onrender.com/update_info.json"

    fun checkForUpdates(currentVersionCode: Int, showToast: Boolean = false) {
        thread {
            try {
                val response = URL(updateUrl).readText()
                val json = JSONObject(response)
                val latestVersion = json.getInt("versionCode")
                val apkUrl = json.getString("apkUrl")
                
                Handler(Looper.getMainLooper()).post {
                    if (latestVersion > currentVersionCode) {
                        Toast.makeText(context, "Actualización encontrada. Iniciando descarga...", Toast.LENGTH_LONG).show()
                        downloadUpdate(apkUrl)
                    } else if (showToast) {
                        Toast.makeText(context, "El sistema ya está en su última versión.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    if (showToast) Toast.makeText(context, "Error conectando con el servidor maestro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadUpdate(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Actualización King System Pro")
            .setDescription("Descargando la nueva versión...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "InDriveBot_Update.apk")
            .setAllowedOverMetered(true) // Fundamental para descargar directamente vía datos móviles
            .setAllowedOverRoaming(true)
        
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}
