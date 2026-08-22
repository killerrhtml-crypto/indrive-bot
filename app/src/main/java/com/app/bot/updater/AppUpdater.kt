package com.app.bot.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AppUpdater(private val context: Context) {

    // URL directa al release en GitHub o al JSON de control
    private val updateCheckUrl = "https://github.com/killerrhtml-crypto/indrive-bot/releases/tag/latest"

    fun checkForUpdatesManual() {
        Toast.initSafe(context, "Verificando actualizaciones...", Toast.LENGTH_SHORT)
        
        Thread {
            try {
                val url = URL("https://raw.githubusercontent.com/killerrhtml-crypto/indrive-bot/main/update_info.json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = reader.readLine() } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Si hay conexión y responde correctamente
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "La aplicación está al día o puedes revisar el repositorio.", Toast.LENGTH_LONG).show()
                        openUpdatePage()
                    }
                } else {
                    throw Exception("Servidor no disponible")
                }
            } catch (e: Exception) {
                // Modo offline o sin conexión: la app no se cae ni bloquea nada
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Sin conexión a internet. Funcionando en modo local.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun openUpdatePage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateCheckUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Evita cierre forzoso si no hay navegador disponible
        }
    }
}
