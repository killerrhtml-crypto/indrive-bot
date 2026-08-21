package com.app.bot.config

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Gestor centralizado de configuración local.
 * Prioridad: config.json local → servidor (si existe versión nueva)
 */
class ConfigurationManager(private val context: Context) {

    private val configFile = File(context.filesDir, CONFIG_FILENAME)
    private val client = OkHttpClient()
    private var cachedConfig: JSONObject? = null

    companion object {
        private const val CONFIG_FILENAME = "config.json"
        private const val TAG = "ConfigManager"
        private const val DEFAULT_CONFIG_VERSION = "1.0"
    }

    /**
     * Obtiene la configuración local si existe, sino retorna configuración por defecto
     */
    fun getLocalConfig(): JSONObject {
        return try {
            if (configFile.exists()) {
                val configJson = JSONObject(configFile.readText(StandardCharsets.UTF_8))
                Log.d(TAG, "✅ Configuración local cargada: versión ${configJson.optString("version")}")
                cachedConfig = configJson
                return configJson
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error leyendo config.json local", e)
        }

        // Retornar configuración por defecto si no existe
        return createDefaultConfig()
    }

    /**
     * Sincroniza con el servidor si hay una versión más nueva
     * No bloquea si falla la descarga - mantiene la config local
     */
    suspend fun syncConfigFromServer(backendUrl: String): JSONObject {
        return withContext(Dispatchers.IO) {
            try {
                val localConfig = getLocalConfig()
                val localVersion = localConfig.optString("version", DEFAULT_CONFIG_VERSION)

                Log.d(TAG, "🔄 Sincronizando config desde servidor: $backendUrl")

                val request = Request.Builder()
                    .url("$backendUrl/api/config/latest")
                    .addHeader("X-Client-Version", localVersion)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val remoteConfig = JSONObject(response.body?.string() ?: "{}")
                    val remoteVersion = remoteConfig.optString("version", DEFAULT_CONFIG_VERSION)

                    if (remoteVersion > localVersion) {
                        Log.i(TAG, "✅ Nueva versión disponible: $remoteVersion")
                        saveConfigLocally(remoteConfig)
                        cachedConfig = remoteConfig
                        return@withContext remoteConfig
                    } else {
                        Log.d(TAG, "ℹ️ Config local es la más reciente")
                    }
                } else {
                    Log.w(TAG, "⚠️ Error del servidor (${response.code}). Usando config local")
                }

                response.close()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error sincronizando config: ${e.message}. Usando local", e)
            }

            // Siempre retornar la config local si hay error
            return@withContext getLocalConfig()
        }
    }

    /**
     * Guarda la configuración en el almacenamiento local
     */
    fun saveConfigLocally(config: JSONObject) {
        try {
            configFile.writeText(config.toString(4), StandardCharsets.UTF_8)
            Log.d(TAG, "💾 Configuración guardada en: ${configFile.absolutePath}")
            cachedConfig = config
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando configuración", e)
        }
    }

    /**
     * Valida e inicializa la config por defecto
     */
    private fun createDefaultConfig(): JSONObject {
        val defaultConfig = JSONObject()
            .put("version", DEFAULT_CONFIG_VERSION)
            .put("mode", "BASIC")
            .put("appName", "InDrive Bot")
            .put("supportedFeatures", listOf(
                "automation",
                "basic_analytics"
            ))
            .put("apiEndpoint", "")
            .put("maxConcurrentTasks", 1)
            .put("updateCheckInterval", 86400) // 24 horas en segundos

        Log.d(TAG, "📋 Usando configuración por defecto")
        return defaultConfig
    }

    /**
     * Obtiene un valor de la configuración (cacheado en memoria)
     */
    fun getString(key: String, default: String = ""): String {
        return (cachedConfig ?: getLocalConfig()).optString(key, default)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return (cachedConfig ?: getLocalConfig()).optInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return (cachedConfig ?: getLocalConfig()).optBoolean(key, default)
    }

    /**
     * Reinicia la configuración (para testing o reset)
     */
    fun resetConfig() {
        try {
            if (configFile.exists()) {
                configFile.delete()
                cachedConfig = null
                Log.d(TAG, "🔄 Configuración reiniciada")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error reseteando configuración", e)
        }
    }
}
