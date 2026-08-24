package com.killerrhtml.indrivebot

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class AdminDashboardActivity : AppCompatActivity() {

    private val drivers = mutableListOf(
        Driver("driver-1", "Carlos Mendoza", "carlos@example.com", DriverStatus.PENDING),
        Driver("driver-2", "Ana Torres", "ana@example.com", DriverStatus.ACTIVE),
        Driver("driver-3", "Luis Herrera", "luis@example.com", DriverStatus.REJECTED)
    )
    private lateinit var adapter: DriversAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<TextView>(R.id.adminAccount).text =
            getString(R.string.admin_account, ADMIN_EMAIL)

        adapter = DriversAdapter(
            drivers = drivers,
            onDateRequested = ::showDatePicker,
            onStatusChanged = ::changeDriverStatus
        )
        findViewById<RecyclerView>(R.id.driversList).apply {
            layoutManager = LinearLayoutManager(this@AdminDashboardActivity)
            adapter = this@AdminDashboardActivity.adapter
        }

        val messageInput = findViewById<EditText>(R.id.supportMessageInput)
        findViewById<Button>(R.id.sendSupportButton).setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isEmpty()) {
                messageInput.error = getString(R.string.support_message_required)
                return@setOnClickListener
            }
            messageInput.text.clear()
            Toast.makeText(this, R.string.support_message_sent, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker(position: Int) {
        val selectedDate = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(year, month, day, 23, 59, 59)
                val driver = drivers[position]
                val expiry = selectedDate.timeInMillis
                updateDriver(driver, driver.status, expiry) {
                    driver.licenseExpiry = expiry
                    adapter.updateExpiry(position, expiry)
                }
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun changeDriverStatus(position: Int, status: DriverStatus) {
        val driver = drivers[position]
        updateDriver(driver, status, driver.licenseExpiry) {
            adapter.updateStatus(position, status)
            Toast.makeText(this, R.string.driver_status_updated, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDriver(
        driver: Driver,
        status: DriverStatus,
        expiry: Long?,
        onSuccess: () -> Unit
    ) {
        ApiClient.updateDriverStatus(
            driverId = driver.id,
            status = status,
            expirationDate = expiry?.let { API_DATE_FORMAT.format(it) }
        ) { success, message ->
            runOnUiThread {
                if (success) onSuccess()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ADMIN_EMAIL = "killerrhtml@gmail.com"
        private val API_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
    }
}