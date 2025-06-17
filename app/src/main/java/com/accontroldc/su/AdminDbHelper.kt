package com.accontroldc.su

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE admin_users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)"
        )
        db.execSQL(
            """
            CREATE TABLE gpio_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                led INTEGER,
                cantidad_encendidos INTEGER,
                timestamp TEXT
            )
            """.trimIndent()
        )

        // Usuario por defecto
        val values = ContentValues().apply {
            put("username", "admin")
            put("password", "1234")
        }
        db.insert("admin_users", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS admin_users")
        db.execSQL("DROP TABLE IF EXISTS gpio_log")
        onCreate(db)
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM admin_users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    companion object {
        const val DB_NAME = "admin_db"
        const val DB_VERSION = 2
    }
}
