package com.app.bot.updater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class DownloadCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            
            val lastDownloadId = context?.getSharedPreferences("updates", Context.MODE_PRIVATE)
                ?.getLong("last_download_id", -1L) ?: -1L
            
            if (downloadId == lastDownloadId && context != null) {
                Log.d("DownloadReceiver", "Descarga completada con éxito: $downloadId")
                val updater = AppUpdater(context)
                updater.installApk()
                Toast.makeText(context, "Actualización descargada. Iniciando instalación...", Toast.LENGTH_LONG).show()
            }
        }
    }
}
