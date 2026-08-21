package com.app.bot.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.bot.AppUpdater
import com.app.bot.R

class UpdatesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_updates, container, false)
        val status = view.findViewById<TextView>(R.id.updateStatus)
        view.findViewById<Button>(R.id.checkUpdatesButton).setOnClickListener {
            status.setText(R.string.update_checking)
            AppUpdater.checkForUpdate(requireContext()) { update ->
                requireActivity().runOnUiThread {
                    AppUpdater.downloadUpdate(requireContext().applicationContext, update)
                    status.text = getString(R.string.update_download_started, update.versionName)
                    Toast.makeText(requireContext(), R.string.update_download_started_short, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }
}
