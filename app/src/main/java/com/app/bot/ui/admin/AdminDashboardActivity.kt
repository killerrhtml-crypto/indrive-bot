package com.app.bot.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.bot.R

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var updateManager: UpdateManager
    private lateinit var botController: BotController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val localTvStatus = findViewById<TextView>(R.id.tvStatus)
        val localTvLog = findViewById<TextView>(R.id.tvLog)
        val localBtnToggleBot = findViewById<Button>(R.id.btnToggleBot)
        val localBtnCheckUpdate = findViewById<ImageView>(R.id.btnCheckUpdate)

        updateManager = UpdateManager(this, localTvLog)
        botController = BotController(this, localTvStatus, localTvLog, localBtnToggleBot)

        localBtnToggleBot.setOnClickListener {
            botController.toggle()
        }

        localBtnCheckUpdate.setOnClickListener {
            updateManager.checkForUpdates()
        }
    }
}
