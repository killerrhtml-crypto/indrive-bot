package com.app.bot

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object ApiClient {

    private const val BASE_URL = "https://tu-servidor-backend.com"
    private const val ADMIN_EMAIL = "killerrhtml@gmail.com"
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun checkDriverLicense(driverEmail: String, onResult: (Boolean, String) -> Unit) {
        val body = JSONObject().put("driverEmail", driverEmail)
        post("/api/license/check", body) { response ->
            val json = response.json
            json.optBoolean("allowed", false) to
                json.optString("message", response.defaultMessage)
        }.also { callback ->
            callback(onResult)
        }
    }

    fun updateDriverStatus(
        driverId: String,
        status: DriverStatus,
        expirationDate: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        val body = JSONObject()
            .put("adminEmail", ADMIN_EMAIL)
            .put("driverId", driverId)
            .put("status", status.name)
        if (expirationDate != null) body.put("expirationDate", expirationDate)

        post("/api/admin/update-driver", body) { response ->
            val json = response.json
            json.optBoolean("success", false) to
                json.optString("message", response.defaultMessage)
        }.also { callback ->
            callback(onResult)
        }
    }

    private fun post(
        path: String,
        body: JSONObject,
        parse: (ApiResponse) -> Pair<Boolean, String>
    ): ((Boolean, String) -> Unit) -> Unit {
        return { onResult ->
            val request = Request.Builder()
                .url(BASE_URL + path)
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, exception: IOException) {
                    onResult(false, "Error de red/servidor")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val responseBody = it.body?.string().orEmpty()
                        val json = runCatching { JSONObject(responseBody) }.getOrDefault(JSONObject())
                        val result = parse(ApiResponse(it.isSuccessful, it.code, json))
                        onResult(result.first, result.second)
                    }
                }
            })
        }
    }

    private data class ApiResponse(val successful: Boolean, val code: Int, val json: JSONObject) {
        val defaultMessage: String
            get() = if (successful) "Operación completada" else "Error de API ($code)"
    }
}