package com.accontroldc.su

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.*

class Led2Activity : AppCompatActivity() {

    private val gpioPin = 2 // LED 2 GPIO
    var estadoAnteriorLed2: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led1)

        val switchLed = findViewById<Switch>(R.id.switchLed)

        switchLed.setOnCheckedChangeListener { _, isChecked ->
            val state = if (isChecked) 1 else 0

            if (estadoAnteriorLed2 == null || estadoAnteriorLed2 != isChecked) {
                GpioState.led2 = isChecked
                runRootCommand("gpioset gpiochip0 $gpioPin=$state")

                registrarEncendido(isChecked)

                estadoAnteriorLed2 = isChecked
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
            put("led", 2)
            put("cantidad_encendidos", if (encendido) 1 else 0)

        }

        db.insert("gpio_log", null, values)
    }

}
