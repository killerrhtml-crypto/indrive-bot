package com.app.bot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class InDriveBotService : AccessibilityService() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Servicio de accesibilidad activado")
        connectWebSocket()
    }

    private fun connectWebSocket() {
        val preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE)
        val url = preferences.getString(
            MainActivity.BACKEND_URL_KEY,
            MainActivity.DEFAULT_BACKEND_URL
        ).orEmpty()

        if (!url.startsWith("ws://") && !url.startsWith("wss://")) {
            Log.e(TAG, "La URL del backend debe usar ws:// o wss://")
            return
        }

        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket conectado")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "Error en WebSocket", t)
            }
        })
    }

    private fun handleMessage(text: String) {
        try {
            val json = JSONObject(text)
            if (json.optString("type") != "CLICK_COMMAND") return

            val x = json.optDouble("x", Double.NaN).toFloat()
            val y = json.optDouble("y", Double.NaN).toFloat()
            if (x.isFinite() && y.isFinite() && x >= 0f && y >= 0f) {
                performClickAt(x, y)
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Comando WebSocket inválido", exception)
        }
    }

    private fun performClickAt(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 50)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() {
        webSocket?.close(NORMAL_CLOSURE, "Servicio interrumpido")
        webSocket = null
    }

    override fun onDestroy() {
        webSocket?.close(NORMAL_CLOSURE, "Servicio detenido")
        client.dispatcher.executorService.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "BotService"
        private const val NORMAL_CLOSURE = 1000
    }
}