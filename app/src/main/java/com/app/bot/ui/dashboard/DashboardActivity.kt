package com.app.bot.ui.dashboard

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.app.bot.R
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

class DashboardActivity : AppCompatActivity() {

    private var isRunning = false
    private var downloadId: Long = -1L

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
                btnToggleBot.text = "DETENER NÚCLEO"
                btnToggleBot.setBackgroundColor(Color.parseColor("#EF4444"))
                tvLog.append("\n[Bot] Núcleo iniciado correctamente.")
            } else {
                tvStatus.text = "Inactivo"
                btnToggleBot.text = "INICIAR NÚCLEO"
                btnToggleBot.setBackgroundColor(Color.parseColor("#38BDF8"))
                tvLog.append("\n[Bot] Núcleo detenido.")
            }
        }

        btnOpenUpdateModal.setOnClickListener {
            checkVersionAndShowUpdate(tvLog)
        }

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    installDownloadedApk(tvLog)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)
        } else {
            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun checkVersionAndShowUpdate(tvLog: TextView) {
        Toast.makeText(this, "Verificando actualizaciones en la nube...", Toast.LENGTH_SHORT).show()

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

                    val jsonStr = response.toString()
                    
                    // Extracción simple de versionCode y versionName del JSON sin librerías pesadas
                    val remoteVersionCode = extractJsonInt(jsonStr, "versionCode")
                    val remoteVersionName = extractJsonString(jsonStr, "versionName")

                    // Obtener la versión instalada actualmente en el dispositivo
                    val pInfo = packageManager.getPackageInfo(packageName, 0)
                    val localVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        pInfo.longVersionCode.toInt()
                    } else {
                        @Suppress("DEPRECATION")
                        pInfo.versionCode
                    }

                    Handler(Looper.getMainLooper()).post {
                        if (remoteVersionCode > localVersionCode) {
                            // Hay una versión más nueva disponible
                            showUpdateDialog(remoteVersionName, tvLog)
                        } else {
                            // Ya tienes la última versión o superior
                            Toast.makeText(this, "Ya tienes la última versión instalada ($localVersionCode)", Toast.LENGTH_LONG).show()
                            tvLog.append("\n[OTA] Sistema al día. Versión actual: $localVersionCode")
                        }
                    }
                } else {
                    throw Exception()
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Error al verificar versión con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showUpdateDialog(newVersion: String, tvLog: TextView) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val tvDetails = dialogView.findViewById<TextView>(R.id.tvUpdateDetails)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBarUpdate)
        val tvProgressText = dialogView.findViewById<TextView>(R.id.tvProgressText)

        tvDetails.text = "¡Nueva versión disponible ($newVersion)!\n• Se detectaron mejoras en el núcleo.\n• Instalación directa In-App."

        val dialog = AlertDialog.Builder(this)
            .setTitle("Actualización OTA Disponible")
            .setView(dialogView)
            .setPositiveButton("Actualizar Ahora") { _, _ ->
                tvLog.append("\n[OTA] Descargando versión $newVersion...")
                downloadAndTrackApk(progressBar, tvProgressText, tvLog)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun downloadAndTrackApk(progressBar: ProgressBar, tvProgressText: TextView, tvLog: TextView) {
        try {
            val apkUrl = "https://github.com/killerrhtml-crypto/indrive-bot/releases/download/latest/app-debug.apk"
            val destination = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "indrive_update.apk")
            if (destination.exists()) destination.delete()

            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("InDrive Bot Actualización")
                .setDescription("Actualizando aplicación...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(destination))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = manager.enqueue(request)

            Thread {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        if (bytesTotal > 0) {
                            val progress = ((bytesDownloaded * 100L) / bytesTotal).toInt()
                            Handler(Looper.getMainLooper()).post {
                                progressBar.progress = progress
                                tvProgressText.text = "Progreso: $progress%"
                            }
                        }

                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                            downloading = false
                        }
                    }
                    cursor.close()
                    Thread.sleep(300)
                }
            }.start()

        } catch (e: Exception) {
            Toast.makeText(this, "Error de descarga: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun installDownloadedApk(tvLog: TextView) {
        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "indrive_update.apk")
            if (!file.exists()) return

            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "${applicationId}.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            tvLog.append("\n[OTA] Lanzando instalador seguro...")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al instalar paquete: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Funciones auxiliares para parsear el JSON de forma nativa sin librerías externas
    private fun extractJsonInt(json: String, key: String): Int {
        try {
            val regex = "\"$key\"\\s*:\\s*(\\d+)".toRegex()
            val match = regex.find(json)
            return match?.groups?.get(1)?.value?.toInt() ?: 1
        } catch (e: Exception) {
            return 1
        }
    }

    private fun extractJsonString(json: String, key: String): String {
        try {
            val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val match = regex.find(json)
            return match?.groups?.get(1)?.value ?: "1.0.0"
        } catch (e: Exception) {
            return "1.0.0"
        }
    }
}
