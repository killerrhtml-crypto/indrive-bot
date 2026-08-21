package com.app.bot

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object AppUpdater {

    private const val UPDATE_INFO_URL =
        "https://indrive-bot-updates.onrender.com/updates/update_info.json"
    private const val APK_FILE_NAME = "indrive-bot-update.apk"
    private val client = OkHttpClient()

    fun checkForUpdate(context: Context, onUpdateAvailable: (UpdateInfo) -> Unit = {}) {
        val request = Request.Builder().url(UPDATE_INFO_URL).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, exception: IOException) = Unit

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) return
                    val info = runCatching {
                        JSONObject(it.body?.string().orEmpty()).let { json ->
                            UpdateInfo(
                                versionCode = json.getInt("latestVersionCode"),
                                versionName = json.getString("latestVersionName"),
                                apkUrl = json.getString("apkUrl"),
                                releaseNotes = json.optString("releaseNotes")
                            )
                        }
                    }.getOrNull() ?: return

                    if (info.versionCode > installedVersionCode(context) && info.apkUrl.startsWith("https://")) {
                        onUpdateAvailable(info)
                    }
                }
            }
        })
    }

    fun downloadUpdate(context: Context, updateInfo: UpdateInfo): Long {
        val request = DownloadManager.Request(Uri.parse(updateInfo.apkUrl))
            .setTitle("Actualización disponible ${updateInfo.versionName}")
            .setDescription("Descargando la nueva versión de InDrive Bot")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILE_NAME)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val manager = context.getSystemService(DownloadManager::class.java)
        return manager.enqueue(request)
    }

    private fun installedVersionCode(context: Context): Long {
        return context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
    }

    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val apkUrl: String,
        val releaseNotes: String
    )
}