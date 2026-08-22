package com.app.bot.updater

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

class AppUpdater(private val context: Context) {
    private val updateUrl = "https://indrive-bot.onrender.com/update_info.json"

    fun checkForUpdates(currentVersionCode: Int, onUpdateAvailable: (String) -> Unit) {
        thread {
            try {
                val response = URL(updateUrl).readText()
                val json = JSONObject(response)
                val latestVersion = json.getInt("versionCode")
                val apkUrl = json.getString("apkUrl")

                Handler(Looper.getMainLooper()).post {
                    if (latestVersion > currentVersionCode) {
                        onUpdateAvailable(apkUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadAndInstall(apkUrl: String) {
        val fileName = "KingSystem_Update.apk"
        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (destination.exists()) destination.delete()

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("King System Pro")
            .setDescription("Descargando actualización del sistema...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destination))

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        context.getSharedPreferences("updates", Context.MODE_PRIVATE)
            .edit()
            .putLong("last_download_id", downloadId)
            .apply()

        Toast.makeText(context, "Descargando actualización en segundo plano...", Toast.LENGTH_LONG).show()
    }

    fun installApk() {
        val fileName = "KingSystem_Update.apk"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (!file.exists()) return

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
