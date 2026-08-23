package com.app.bot

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NetworkManager {
    private val client = OkHttpClient()

    suspend fun checkServerUpdate(url: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            // Petición real al servidor asegurando el uso del ancho de banda disponible
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                val version = json.optString("versionName", "Desconocida")
                return@withContext Pair(true, "Versión $version sincronizada")
            }
            return@withContext Pair(false, "Servidor denegó la conexión")
        } catch (e: Exception) {
            return@withContext Pair(false, "Error de enlace: Verifica la red")
        }
    }
}
