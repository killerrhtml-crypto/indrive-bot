package com.app.bot

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private var downloadId: Long = -1L
    private val CHANNEL_ID = "king_system_notifications"
    private val CURRENT_VERSION_NAME = "1.0.6"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        showSecurityPinDialog()
    }

    private fun showSecurityPinDialog() {
        val input = EditText(this).apply {
            hint = "PIN de seguridad (Pruebe: 1234)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
        }

        AlertDialog.Builder(this)
            .setTitle("King System [$CURRENT_VERSION_NAME]")
            .setMessage("Acceso protegido al núcleo del bot:")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Verificar") { _, _ ->
                val pin = input.text.toString()
                if (pin == "1234" || pin == "0000") {
                    Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()
                    loadDashboardView()
                } else {
                    Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                    showSecurityPinDialog()
                }
            }
            .setNegativeButton("Salir") { _, _ -> finish() }
            .show()
    }

    private fun loadDashboardView() {
        try {
            setContentView(R.layout.activity_dashboard)
            Toast.makeText(this, "King System v$CURRENT_VERSION_NAME Operativo", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this, "Núcleo activado", Toast.LENGTH_SHORT).show()
                    sendLocalNotification("King System [$CURRENT_VERSION_NAME]", "Núcleo activado y operando con red de datos.")
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "King System Alertas"
            val descriptionText = "Canal para notificaciones de actualizaciones"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendLocalNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun checkCloudBuildStatus() {
        Toast.makeText(this, "Consultando estado del Build...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val url = java.net.URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/build_status.json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = reader.readLine() } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonStr = response.toString()
                    Handler(Looper.getMainLooper()).post {
                        AlertDialog.Builder(this)
                            .setTitle("Estado del Build [Firmado v$CURRENT_VERSION_NAME]")
                            .setMessage("Datos del servidor:\n$jsonStr")
                            .setPositiveButton("Cerrar", null)
                            .show()
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this, "Servidor respondió con código: ${connection.responseCode}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun checkVersionAndShowUpdate() {
        Toast.makeText(this, "Verificando actualizaciones OTA...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val url = java.net.URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/update_info.json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = reader.readLine() } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonStr = response.toString()
                    val remoteVersionName = extractJsonString(jsonStr, "versionName")
                    val remoteVersionCode = extractJsonInt(jsonStr, "versionCode")

                    Handler(Looper.getMainLooper()).post {
                        if (remoteVersionName.isNotEmpty()) {
                            showUpdateDialog(remoteVersionName, remoteVersionCode)
                        } else {
                            Toast.makeText(this, "No se pudo leer la versión del servidor", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Error al conectar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun showUpdateDialog(newVersion: String, versionCode: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val tvDetails = dialogView.findViewById<TextView>(R.id.tvUpdateDetails)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBarUpdate)
        val tvProgressText = dialogView.findViewById<TextView>(R.id.tvProgressText)

        tvDetails.text = "Firmada Actual: v$CURRENT_VERSION_NAME\nServidor Remoto: v$newVersion (Code: $versionCode)\n• Actualización lista para descargar."

        AlertDialog.Builder(this)
            .setTitle("King System Actualización [v$newVersion]")
            .setView(dialogView)
            .setPositiveButton("Actualizar Ahora") { _, _ -> downloadAndTrackApk(progressBar, tvProgressText) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun downloadAndTrackApk(progressBar: ProgressBar, tvProgressText: TextView) {
        try {
            val apkUrl = "https://github.com/killerrhtml-crypto/indrive-bot/releases/download/latest/app-debug.apk"
            val destination = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "king_system_update.apk")
            if (destination.exists()) destination.delete()

            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("King System v$CURRENT_VERSION_NAME Update")
                .setDescription("Descargando APK firmado...")
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

    private fun installDownloadedApk() {
        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "king_system_update.apk")
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
            Toast.makeText(this, "Error al instalar APK: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showCommitHistoryDialog() {
        AlertDialog.Builder(this)
            .setTitle("Historial Firmado [v$CURRENT_VERSION_NAME]")
            .setMessage("• v$CURRENT_VERSION_NAME: Corrección de compilación y firmas de versión visibles.")
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun showNotificationsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Centro de Notificaciones")
            .setMessage("• [Sistema] Versión firmada activa: v$CURRENT_VERSION_NAME\n• [Red] Operando con red de datos móviles.")
            .setPositiveButton("Limpiar") { _, _ ->
                Toast.makeText(this, "Notificaciones limpiadas", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun extractJsonInt(json: String, key: String): Int {
        return try {
            val index = json.indexOf("\"$key\"")
            if (index == -1) return 1
            val sub = json.substring(index)
            val colon = sub.indexOf(":")
            val comma = sub.indexOfAny(charArrayOf(',', '}'))
            sub.substring(colon + 1, comma).trim().toInt()
        } catch (e: Exception) { 1 }
    }

    private fun extractJsonString(json: String, key: String): String {
        return try {
            val index = json.indexOf("\"$key\"")
            if (index == -1) return ""
            val sub = json.substring(index)
            val firstQuote = sub.indexOf("\"", sub.indexOf(":") + 1)
            val secondQuote = sub.indexOf("\"", firstQuote + 1)
            sub.substring(firstQuote + 1, secondQuote)
        } catch (e: Exception) { "" }
    }
}
