package com.app.bot.ui.dashboard

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

class DashboardActivity : AppCompatActivity() {

    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvLog = findViewById<TextView>(R.id.tvLog)
        val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)
        val btnOpenUpdateModal = findViewById<ImageView>(R.id.btnOpenUpdateModal)

        btnToggleBot.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) {
                tvStatus.text = "Activo y Operando"
                btnToggleBot.text = "DETENER BOT"
                btnToggleBot.setBackgroundColor(Color.parseColor("#D32F2F"))
                tvLog.append("\n[Bot] Motor iniciado correctamente.")
            } else {
                tvStatus.text = "Inactivo"
                btnToggleBot.text = "INICIAR BOT"
                btnToggleBot.setBackgroundColor(Color.parseColor("#00E676"))
                tvLog.append("\n[Bot] Motor detenido.")
            }
        }

        btnOpenUpdateModal.setOnClickListener {
            showUpdateDialog(tvLog)
        }
    }

    private fun showUpdateDialog(tvLog: TextView) {
        Toast.makeText(this, "Consultando información de versión...", Toast.LENGTH_SHORT).show()

        Thread {
            try {
                val url = java.net.URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/update_info.json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 4000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = reader.readLine() } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                    Handler(Looper.getMainLooper()).post {
                        AlertDialog.Builder(this)
                            .setTitle("Gestión de Actualización OTA")
                            .setMessage("Detalles de la versión en la nube:\n\n• Fecha de verificación: $currentDate\n• Estado: Disponible para descarga directa\n• Servidor: GitHub Releases (In-App)")
                            .setPositiveButton("Descargar e Instalar") { _, _ ->
                                tvLog.append("\n[OTA] Iniciando descarga en segundo plano...")
                                downloadAndInstallApk(tvLog)
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                } else {
                    throw Exception()
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "No se pudo conectar con el servidor de versiones", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun downloadAndInstallApk(tvLog: TextView) {
        try {
            val apkUrl = "https://github.com/killerrhtml-crypto/indrive-bot/releases/download/latest/app-debug.apk"
            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("InDrive Bot Update")
                .setDescription("Actualizando aplicación...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "indrive_update.apk")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            tvLog.append("\n[OTA] APK guardado en Descargas.")
            Toast.makeText(this, "Descarga iniciada en segundo plano", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
