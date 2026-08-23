package com.app.bot

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "king_system_enterprise.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_TELEMETRY = "telemetry_records"
        const val COL_ID = "id"
        const val COL_DEVICE_ID = "device_id"
        const val COL_IP_ADDRESS = "ip_address"
        const val COL_EMAIL = "email"
        const val COL_PHONE = "phone"
        const val COL_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TELEMETRY (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DEVICE_ID TEXT,
                $COL_IP_ADDRESS TEXT,
                $COL_EMAIL TEXT,
                $COL_PHONE TEXT,
                $COL_TIMESTAMP TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TELEMETRY")
        onCreate(db)
    }

    fun insertRecord(deviceId: String, ip: String, email: String, phone: String, timestamp: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_DEVICE_ID, deviceId)
            put(COL_IP_ADDRESS, ip)
            put(COL_EMAIL, email)
            put(COL_PHONE, phone)
            put(COL_TIMESTAMP, timestamp)
        }
        return db.insert(TABLE_TELEMETRY, null, values)
    }
}
