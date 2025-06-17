package com.accontroldc.su

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.os.Handler
import android.content.ContentValues
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnLed1: Button
    private lateinit var btnLed2: Button
    private lateinit var btnLed3: Button
    private lateinit var btnAdmin: Button
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        statusText = findViewById(R.id.statusText)
        btnLed1 = findViewById(R.id.btnLed1)
        btnLed2 = findViewById(R.id.btnLed2)
        btnLed3 = findViewById(R.id.btnLed3)
        btnAdmin = findViewById(R.id.btnAdmin)


        btnLed1.setOnClickListener {
            startActivity(Intent(this, Led1Activity::class.java))
        }

        btnLed2.setOnClickListener {
            startActivity(Intent(this, Led2Activity::class.java))
        }

        btnLed3.setOnClickListener {
            startActivity(Intent(this, Led3Activity::class.java))
        }
        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java)) // <- abre login admin
        }

    }

    override fun onResume() {
        super.onResume()
        updateStatus()
        startRegistroAutomatico()
    }

    private fun updateStatus() {
        statusText.text = """
            LED 1: ${if (GpioState.led1) "Encendido" else "Apagado"}
            LED 2: ${if (GpioState.led2) "Encendido" else "Apagado"}
            LED 3: ${if (GpioState.led3) "Encendido" else "Apagado"}
        """.trimIndent()
    }

    private fun startRegistroAutomatico() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                registrarEstado()
                handler.postDelayed(this, AdminConfig.intervaloMinutos * 60 * 1000L)
            }
        }
        handler.post(runnable)
    }

    private fun registrarEstado() {
        val dbHelper = AdminDbHelper(this)
        val db = dbHelper.writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        fun logEstado(led: Int, estado: Boolean) {
            if (estado) {
                val values = ContentValues().apply {
                    put("led", led)
                    put("cantidad_encendidos", 1)
                    put("timestamp", currentTime)
                }
                db.insert("gpio_log", null, values)
            }
        }

        logEstado(1, GpioState.led1)
        logEstado(2, GpioState.led2)
        logEstado(3, GpioState.led3)
    }
}
