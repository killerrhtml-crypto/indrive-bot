package com.app.bot

data class Driver(
    val id: String,
    val name: String,
    val email: String,
    var status: DriverStatus,
    var licenseExpiry: Long? = null
)

enum class DriverStatus {
    PENDING,
    ACTIVE,
    REJECTED
}