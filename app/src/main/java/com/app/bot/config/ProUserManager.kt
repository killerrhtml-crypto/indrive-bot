package com.app.bot.config

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gestor de autenticación PRO/ADMIN
 * Verifica si el usuario actual tiene licencia PRO mediante archivo local .license_pro
 */
class ProUserManager(private val context: Context) {

    private val licenseFile = File(context.filesDir, LICENSE_FILENAME)

    companion object {
        private const val LICENSE_FILENAME = ".license_pro"
        private const val TAG = "ProUserManager"
    }

    /**
     * Verifica si el usuario actual tiene licencia PRO válida
     */
    fun isProUser(): Boolean {
        return try {
            if (!licenseFile.exists()) {
                Log.d(TAG, "❌ Archivo de licencia no encontrado")
                return false
            }

            val licenseJson = JSONObject(licenseFile.readText(StandardCharsets.UTF_8))
            val isValid = validateLicense(licenseJson)

            if (isValid) {
                Log.d(TAG, "✅ Usuario PRO verificado: ${licenseJson.optString("admin_id")}")
            } else {
                Log.w(TAG, "⚠️ Licencia expirada o inválida")
            }

            isValid
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error leyendo licencia PRO", e)
            false
        }
    }

    /**
     * Valida la licencia PRO
     */
    private fun validateLicense(license: JSONObject): Boolean {
        val licenseKey = license.optString("license_key", "")
        val expirationDate = license.optString("expiration", "")

        if (licenseKey.isEmpty() || !licenseKey.startsWith("KING_SYSTEM_")) {
            Log.w(TAG, "⚠️ Clave de licencia inválida")
            return false
        }

        if (expirationDate.isNotEmpty()) {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val expDate = dateFormat.parse(expirationDate)
                val currentDate = Date()

                if (currentDate.after(expDate)) {
                    Log.w(TAG, "⚠️ Licencia expirada: $expirationDate")
                    return false
                }

                Log.d(TAG, "✅ Licencia válida hasta: $expirationDate")
                true
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error validando fecha de expiración", e)
                false
            }
        }

        return true
    }

    /**
     * Obtiene los datos de la licencia PRO
     */
    fun getLicenseData(): JSONObject? {
        return try {
            if (licenseFile.exists()) {
                JSONObject(licenseFile.readText(StandardCharsets.UTF_8))
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo datos de licencia", e)
            null
        }
    }

    /**
     * Obtiene el ID del administrador PRO
     */
    fun getAdminId(): String {
        return getLicenseData()?.optString("admin_id", "unknown") ?: "unknown"
    }

    /**
     * Crea o actualiza el archivo de licencia PRO (solo para desarrollo)
     */
    fun createProLicense(
        licenseKey: String = "KING_SYSTEM_PRO_2026",
        adminId: String = "killerrhtml",
        expiration: String = "2099-12-31"
    ) {
        try {
            val license = JSONObject()
                .put("license_key", licenseKey)
                .put("admin_id", adminId)
                .put("expiration", expiration)
                .put("created_at", Date().toString())

            licenseFile.writeText(license.toString(4), StandardCharsets.UTF_8)
            Log.d(TAG, "✅ Licencia PRO creada para: $adminId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creando licencia PRO", e)
        }
    }

    /**
     * Elimina la licencia PRO (para logout)
     */
    fun removeLicense() {
        try {
            if (licenseFile.exists()) {
                licenseFile.delete()
                Log.d(TAG, "🔄 Licencia PRO eliminada")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando licencia", e)
        }
    }
}
