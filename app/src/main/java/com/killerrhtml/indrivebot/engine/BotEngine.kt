package com.killerrhtml.indrivebot.engine

class BotEngine {
    private var isRunning: Boolean = true

    fun toggleState(): Boolean {
        isRunning = !isRunning
        return isRunning
    }

    fun getStatus(): Boolean = isRunning
}
