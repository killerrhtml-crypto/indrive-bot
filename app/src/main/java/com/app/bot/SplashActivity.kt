package com.app.bot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode

class SplashActivity : AppCompatActivity() {

    private lateinit var sceneView: SceneView
    private lateinit var startButton: Button
    private lateinit var lionAnimation: LottieAnimationView
    private val mainHandler = Handler(Looper.getMainLooper())
    private val showButton = Runnable {
        startButton.visibility = View.VISIBLE
        startButton.animate().alpha(1f).setDuration(500).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sceneView = findViewById(R.id.sceneView3D)
        startButton = findViewById(R.id.btnStart)
        lionAnimation = findViewById(R.id.lottieLion)
        lionAnimation.setAnimation(R.raw.king_system_anim)
        lionAnimation.playAnimation()
        startButton.visibility = View.INVISIBLE
        startButton.alpha = 0f

        AppUpdater.checkForUpdate(this) { updateInfo ->
            AppUpdater.downloadUpdate(applicationContext, updateInfo)
        }
        loadHorseModel()
        mainHandler.postDelayed(showButton, SPLASH_DURATION_MS)
        startButton.setOnClickListener {
            startActivity(Intent(this, LoginPinActivity::class.java))
            finish()
        }
    }

    private fun loadHorseModel() {
        val hasModel = assets.list(ASSET_DIRECTORY)?.contains(MODEL_FILE) == true
        if (!hasModel) return

        sceneView.modelLoader.loadModelInstanceAsync(
            fileLocation = MODEL_FILE,
            onResult = { modelInstance ->
                modelInstance?.let {
                    sceneView.addChildNode(ModelNode(it, autoAnimate = true))
                }
            }
        )
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(showButton)
        lionAnimation.cancelAnimation()
        sceneView.destroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (::lionAnimation.isInitialized) lionAnimation.resumeAnimation()
    }

    override fun onPause() {
        lionAnimation.pauseAnimation()
        super.onPause()
    }

    companion object {
        private const val ASSET_DIRECTORY = ""
        private const val MODEL_FILE = "horse.glb"
        private const val SPLASH_DURATION_MS = 5_000L
    }
}