package com.accontroldc.su

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Led2Activity : AppCompatActivity() {

    private lateinit var estadoText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led2)

        estadoText = findViewById(R.id.estadoText2)

        actualizarEstado()
    }

    override fun onResume() {
        super.onResume()
        actualizarEstado()
    }

    private fun actualizarEstado() {
        val estado = if (GpioState.led2) "Encendido" else "Apagado"
        estadoText.text = "Estado LED 2: $estado"
    }
}
