package com.accontroldc.su

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var intervaloInput: EditText
    private lateinit var btnGuardarIntervalo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        // Inicializamos las vistas
        intervaloInput = findViewById(R.id.intervaloInput)
        btnGuardarIntervalo = findViewById(R.id.btnGuardarIntervalo)

        // Seteamos el texto actual del intervalo para mostrar el valor guardado
        intervaloInput.setText(AdminConfig.intervaloMinutos.toString())

        // Listener para guardar el intervalo
        btnGuardarIntervalo.setOnClickListener {
            val valor = intervaloInput.text.toString().toIntOrNull()

            if (valor != null && valor > 0) {
                AdminConfig.intervaloMinutos = valor
                Toast.makeText(this, "Intervalo actualizado a $valor minutos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Intervalo inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

