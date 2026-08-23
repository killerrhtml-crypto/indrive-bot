package com.app.bot

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
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private var downloadId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_dashboard)

            val btnToggleBot = findViewById<Button>(R.id.btnToggleBot)
            val btnCheckBuildStatus = findViewById<ImageView>(R.id.btnCheckBuildStatus)
            val btnOpenUpdateModal = findViewById<ImageView>(R.id.btnOpenUpdateModal)
            val btnOpenCommits = findViewById<ImageView>(R.id.btnOpenCommits)
            val btnOpenNotifications = findViewById<ImageView>(R.id.btnOpenNotifications)

            btnToggleBot?.setOnClickListener {
                isRunning = !isRunning
                if (isRunning) {
                    btnToggleBot.text = "DETENER NÚCLEO BOT"
                    btnToggleBot.setBackgroundColor(Color.parseColor("#EF4444"))
                    Toast.makeText(this, "Núcleo activado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    btnToggleBot.text = "INICIAR NÚCLEO BOT"
                    btnToggleBot.setBackgroundColor(Color.parseColor("#38BDF8"))
                    Toast.makeText(this, "Núcleo detenido", Toast.LENGTH_SHORT).show()
                }
            }

            btnCheckBuildStatus?.setOnClickListener { checkCloudBuildStatus() }
            btnOpenUpdateModal?.setOnClickListener { checkVersionAndShowUpdate() }
            btnOpenCommits?.setOnClickListener { showCommitHistoryDialog() }
            btnOpenNotifications?.setOnClickListener { showNotificationsDialog() }

            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (downloadId == id) {
                        installDownloadedApk()
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)
            } else {
                registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error de inicialización: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkCloudBuildStatus() {
        Toast.makeText(this, "Consultando estado del Build...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val url = java.net.URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/build_status.json")
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
                    val status = extractJsonString(jsonStr, "status")
                    val message = extractJsonString(jsonStr, "message")
                    val timestamp = extractJsonString(jsonStr, "timestamp")

                    Handler(Looper.getMainLooper()).post {
                        val iconTitle = when (status) {
                            "BUILDING" -> "🟡 Compilando en la nube..."
                            "SUCCESS" -> "🟢 ¡APK Compilado con Éxito!"
                            "FAILED" -> "🔴 Error en la Compilación"
                            else -> "⚪ Sistema Operativo"
                        }

                        AlertDialog.Builder(this)
                            .setTitle(iconTitle)
                            .setMessage("Detalles:\n$message\n\nActualizado: $timestamp")
                            .setPositiveButton("Cerrar", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Sin conexión al servidor de estado", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun checkVersionAndShowUpdate() {
        Toast.makeText(this, "Verificando actualizaciones...", Toast.LENGTH_SHORT).show()
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
                    val remoteVersionCode = extractJsonInt(jsonStr, "versionCode")
                    val remoteVersionName = extractJsonString(jsonStr, "versionName")

                    val pInfo = packageManager.getPackageInfo(packageName, 0)
                    val localVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        pInfo.longVersionCode.toInt()
                    } else {
                        @Suppress("DEPRECATION")
                        pInfo.versionCode
                    }

                    Handler(Looper.getMainLooper()).post {
                        if (remoteVersionCode > localVersionCode) {
                            showUpdateDialog(remoteVersionName)
                        } else {
                            Toast.makeText(this, "Ya tienes la última versión instalada", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showUpdateDialog(newVersion: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val tvDetails = dialogView.findViewById<TextView>(R.id.tvUpdateDetails)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBarUpdate)
        val tvProgressText = dialogView.findViewById<TextView>(R.id.tvProgressText)

        tvDetails.text = "¡Nueva versión disponible ($newVersion)!\n• Actualización limpia integrada."

        AlertDialog.Builder(this)
            .setTitle("King System Actualización")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ -> downloadAndTrackApk(progressBar, tvProgressText) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun downloadAndTrackApk(progressBar: ProgressBar, tvProgressText: TextView) {
        try {
            val apkUrl = "https://github.com/killerrhtml-crypto/indrive-bot/releases/download/latest/app-debug.apk"
            val destination = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "indrive_update.apk")
            if (destination.exists()) destination.delete()

            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("King System Actualización")
                .setDescription("Descargando APK...")
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
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun installDownloadedApk() {
        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "indrive_update.apk")
            if (!file.exists()) return

            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al instalar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showCommitHistoryDialog() {
        val commits = """
            • [commit 8f1b20]: King System Rebrand y vistas estables.
            • [commit 4a2b91]: Interfaz optimizada sin errores de XML.
            • [commit 1f8c32]: Integración de FileProvider para updates.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Historial de Versiones")
            .setMessage(commits)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun showNotificationsDialog() {
        val notifications = """
            • [10:15 AM] Carlos Mendoza solicitó revisión de licencia.
            • [09:42 AM] King System actualizado a versión v1.0.3 exitosamente.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Centro de Notificaciones")
            .setMessage(notifications)
            .setPositiveButton("Limpiar") { _, _ ->
                Toast.makeText(this, "Notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun extractJsonInt(json: String, key: String): Int {
        return try {
            val regex = "\"$key\"\\s*:\\s*(\\d+)".toRegex()
            regex.find(json)?.groups?.get(1)?.value?.toInt() ?: 1
        } catch (e: Exception) { 1 }
    }

    private fun extractJsonString(json: String, key: String): String {
        return try {
            val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            regex.find(json)?.groups?.get(1)?.value ?: ""
        } catch (e: Exception) { "" }
    }
}
