package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.app.bot.config.ConfigurationManager
import com.app.bot.config.ProUserManager
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var sceneView: SceneView
    private lateinit var startButton: Button
    private lateinit var lionAnimation: LottieAnimationView
    private lateinit var tvRole: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var configManager: ConfigurationManager
    private lateinit var proUserManager: ProUserManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val loadingScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val ASSET_DIRECTORY = ""
        private const val MODEL_FILE = "horse.glb"
        private const val SPLASH_DURATION_MS = 5_000L
        private const val VERIFICATION_TIMEOUT_MS = 3_000L
        private const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inicializar vistas
        sceneView = findViewById(R.id.sceneView3D)
        startButton = findViewById(R.id.btnStart)
        lionAnimation = findViewById(R.id.lottieLion)
        tvRole = findViewById(R.id.tvRole)
        progressBar = findViewById(R.id.progressBar)

        // Inicializar managers
        configManager = ConfigurationManager(this)
        proUserManager = ProUserManager(this)

        // Setup animación
        lionAnimation.setAnimation(R.raw.king_system_anim)
        lionAnimation.playAnimation()
        startButton.visibility = View.INVISIBLE
        startButton.alpha = 0f
        progressBar.visibility = View.VISIBLE

        // Cargar modelo 3D
        loadHorseModel()

        // Iniciar verificación en background
        verifyUserAndInitialize()
    }

    /**
     * Verifica licencia PRO y configuración en background
     */
    private fun verifyUserAndInitialize() {
        loadingScope.launch {
            try {
                Log.d(TAG, "🔄 Iniciando verificación de usuario...")

                // Verificar configuración local
                val localConfig = configManager.getLocalConfig()
                Log.d(TAG, "✅ Config cargada: versión ${localConfig.optString("version")}")

                // Verificar licencia PRO
                val isProUser = proUserManager.isProUser()
                val adminId = proUserManager.getAdminId()
                Log.d(TAG, "🔐 Usuario PRO: $isProUser | Admin: $adminId")

                // Actualizar UI con información
                updateUIWithUserRole(isProUser, adminId)

                // Mostrar botón después del splash
                mainHandler.postDelayed({
                    showStartButton()
                }, SPLASH_DURATION_MS)

                // Guardar estado para transición
                val prefs = getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE)
                prefs.edit()
                    .putBoolean("is_pro_user", isProUser)
                    .putString("admin_id", adminId)
                    .apply()

                startButton.setOnClickListener {
                    navigateBasedOnRole(isProUser)
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en verificación: ${e.message}", e)
                // Fallback: usuario básico
                updateUIWithUserRole(false, "unknown")
                mainHandler.postDelayed({ showStartButton() }, SPLASH_DURATION_MS)
            }
        }
    }

    /**
     * Actualiza la UI con el rol del usuario
     */
    private fun updateUIWithUserRole(isProUser: Boolean, adminId: String) {
        mainHandler.post {
            if (isProUser) {
                tvRole.text = "👑 ADMINISTRADOR PRO\n($adminId)"
                tvRole.textSize = 12f
                startButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#FF6B00")
                )
                startButton.text = "PANEL ADMIN"
                Log.d(TAG, "🎯 UI actualizada: Modo PRO")
            } else {
                tvRole.text = "OPERADOR BÁSICO"
                tvRole.textSize = 14f
                startButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#2563EB")
                )
                startButton.text = "INICIAR"
                Log.d(TAG, "🎯 UI actualizada: Modo BÁSICO")
            }
        }
    }

    /**
     * Muestra el botón de inicio
     */
    private fun showStartButton() {
        startButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        startButton.animate().alpha(1f).setDuration(500).start()
    }

    /**
     * Navega a la actividad correcta según el rol
     */
    private fun navigateBasedOnRole(isProUser: Boolean) {
        Log.d(TAG, "🚀 Navegando... (PRO: $isProUser)")
        val intent = if (isProUser) {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, LoginPinActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Carga el modelo 3D del caballo
     */
    private fun loadHorseModel() {
        val hasModel = assets.list(ASSET_DIRECTORY)?.contains(MODEL_FILE) == true
        if (!hasModel) {
            Log.w(TAG, "⚠️ Modelo 3D no encontrado")
            return
        }

        try {
            sceneView.modelLoader.loadModelInstanceAsync(
                fileLocation = MODEL_FILE,
                onResult = { modelInstance ->
                    modelInstance?.let {
                        sceneView.addChildNode(ModelNode(it, autoAnimate = true))
                        Log.d(TAG, "✅ Modelo 3D cargado")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cargando modelo 3D", e)
        }
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks { }
        lionAnimation.cancelAnimation()
        sceneView.destroy()
        loadingScope.launch { }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (::lionAnimation.isInitialized) lionAnimation.resumeAnimation()
    }

    override fun onPause() {
        if (::lionAnimation.isInitialized) lionAnimation.pauseAnimation()
        super.onPause()
    }
}
