package com.app.bot.updater

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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
                // Silencioso en segundo plano
            }
        }
    }

    fun downloadAndInstall(apkUrl: String) {
        val fileName = "KingSystem_Update.apk"
        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (destination.exists()) destination.delete()

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("King System Pro")
            .setDescription("Descargando actualización modular...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destination))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        
        Toast.makeText(context, "Descargando actualización en segundo plano...", Toast.LENGTH_LONG).show()
    }

    fun installApk() {
        val fileName = "KingSystem_Update.apk"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (!file.exists()) return

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}
