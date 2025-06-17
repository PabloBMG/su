package com.accontroldc.su

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnLed1: Button
    private lateinit var btnLed2: Button
    private lateinit var btnLed3: Button
    private lateinit var btnAdmin: Button
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    // GPIOs que vamos a monitorear
    private val gpioList = listOf(17, 2, 3)
    private val gpioToLedMap = mapOf(17 to 1, 2 to 2, 3 to 3)
    private val previousStates = mutableMapOf<Int, Boolean>()

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
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        gpioList.forEach { previousStates[it] = false }
    }

    override fun onResume() {
        super.onResume()
        startMonitoreo()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun startMonitoreo() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                checkAndRegisterGpioStates()
                updateStatus()
                handler.postDelayed(this, 100) // cada segundo
            }
        }
        handler.post(runnable)
    }

    private fun checkAndRegisterGpioStates() {
        val dbHelper = AdminDbHelper(this)
        val db = dbHelper.writableDatabase
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        gpioList.forEach { gpio ->
            val estadoActual = leerEstadoGpio(gpio)
            val estadoAnterior = previousStates[gpio] ?: false

            if (estadoActual != null && estadoActual != estadoAnterior) {
                // Guardar solo si cambia el estado
                val values = ContentValues().apply {
                    put("led", gpioToLedMap[gpio])
                    put("cantidad_encendidos", if (estadoActual) 1 else 0)
                    put("timestamp", timestamp)
                }
                db.insert("gpio_log", null, values)

                // Actualizar variable global
                when (gpio) {
                    17 -> GpioState.led1 = estadoActual
                    2 -> GpioState.led2 = estadoActual
                    3 -> GpioState.led3 = estadoActual
                }

                // Guardar nuevo estado
                previousStates[gpio] = estadoActual
            }
        }
    }

    private fun leerEstadoGpio(gpio: Int): Boolean? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "gpioget gpiochip0 $gpio"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()?.trim()
            process.waitFor()
            result == "1"
        } catch (_: Exception) {
            null
        }
    }

    private fun updateStatus() {
        statusText.text = """
            LED 1: ${if (GpioState.led1) "Encendido" else "Apagado"}
            LED 2: ${if (GpioState.led2) "Encendido" else "Apagado"}
            LED 3: ${if (GpioState.led3) "Encendido" else "Apagado"}
        """.trimIndent()
    }
}
