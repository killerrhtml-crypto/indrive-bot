package com.app.bot

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class AppUpdater(private val context: Context) {
    
    fun downloadUpdate(apkUrl: String, version: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("King System Core [v$version]")
                .setDescription("Descargando núcleo seguro...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                // Restricción estricta: Operar únicamente mediante red de datos móviles
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "KingSystem_v$version.apk")
            
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
            // Aquí luego enlazaremos un indicador visual LED en la UI en lugar de un Toast
        }
    }
}
