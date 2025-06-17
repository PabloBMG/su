package com.accontroldc.su

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.*

class Led3Activity : AppCompatActivity() {

    private val gpioPin = 3 // LED 1 GPIO
    var estadoAnteriorLed3: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led1)

        val switchLed = findViewById<Switch>(R.id.switchLed)

        switchLed.setOnCheckedChangeListener { _, isChecked ->
            val state = if (isChecked) 1 else 0

            if (estadoAnteriorLed3 == null || estadoAnteriorLed3 != isChecked) {
                GpioState.led3 = isChecked
                runRootCommand("gpioset gpiochip0 $gpioPin=$state")

                registrarEncendido(isChecked)

                estadoAnteriorLed3 = isChecked
            }
        }
    }

    private fun runRootCommand(command: String) {
        Thread {
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", command)).waitFor()
            } catch (_: Exception) {}
        }.start()
    }

    private fun registrarEncendido(encendido: Boolean) {
        val dbHelper = AdminDbHelper(this)
        val db = dbHelper.writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put("timestamp", currentTime)
            put("led", 3)
            put("cantidad_encendidos", if (encendido) 1 else 0)

        }

        db.insert("gpio_log", null, values)
    }

}
