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
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) return@withContext Pair(true, "Servidor conectado")
            return@withContext Pair(false, "Denegado")
        } catch (e: Exception) { return@withContext Pair(false, "Error de red móvil") }
    }
}
