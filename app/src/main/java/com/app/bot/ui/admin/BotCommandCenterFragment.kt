package com.app.bot.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.bot.R

/**
 * Bot Command Center - Centro de comandos para automatización
 */
class BotCommandCenterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bot_command_center, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCommand = view.findViewById<EditText>(R.id.etCommand)
        val btnSendCommand = view.findViewById<Button>(R.id.btnSendCommand)
        val tvCommandOutput = view.findViewById<TextView>(R.id.tvCommandOutput)

        btnSendCommand?.setOnClickListener {
            val command = etCommand?.text?.toString() ?: "NO_COMMAND"
            tvCommandOutput?.text = "📤 Enviado: $command\n✅ Estado: Procesando..."
            etCommand?.setText("")
        }
    }
}
