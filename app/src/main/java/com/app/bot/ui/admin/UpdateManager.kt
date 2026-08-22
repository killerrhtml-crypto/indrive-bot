package com.app.bot.ui.admin

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class UpdateManager(private val context: Context, private val tvLog: TextView) {

    fun checkForUpdates() {
        Toast.makeText(context, "Buscando actualizaciones en la nube...", Toast.LENGTH_SHORT).show()
        tvLog.append("\n[OTA] Consultando repositorios...")

        Thread {
            try {
                val url = URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/update_info.json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 4000
                connection.readTimeout = 4000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = reader.readLine() } != null) {
                        response.append(line)
                    }
                    reader.close()

                    Handler(Looper.getMainLooper()).post {
                        tvLog.append("\n[OTA] Versión más reciente disponible.")
                        Toast.makeText(context, "Descargando actualización en segundo plano...", Toast.LENGTH_LONG).show()
                        downloadAndInstallApk("https://github.com/killerrhtml-crypto/indrive-bot/releases/download/latest/app-debug.apk")
                    }
                } else {
                    throw Exception("Error de conexión con el servidor")
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    tvLog.append("\n[OTA] Error: No se pudo verificar la actualización.")
                    Toast.makeText(context, "No se pudo conectar con el servidor OTA", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun downloadAndInstallApk(apkUrl: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("Actualización InDrive Bot")
                .setDescription("Descargando nueva versión...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "indrive_update.apk")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            
            tvLog.append("\n[OTA] APK descargado en la carpeta de Descargas.")
        } catch (e: Exception) {
            Toast.makeText(context, "Error al descargar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
