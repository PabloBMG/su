package com.accontroldc.su

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        val intervaloInput = findViewById<EditText>(R.id.intervaloInput)
        val btnGuardarIntervalo = findViewById<Button>(R.id.btnGuardarIntervalo)
        val btnVerLogs = findViewById<Button>(R.id.btnVerLogs)
        val textLogs = findViewById<TextView>(R.id.textLogs)

        // Mostrar valor actual del intervalo al iniciar
        intervaloInput.setText(AdminConfig.intervaloMinutos.toString())

        btnGuardarIntervalo.setOnClickListener {
            val valor = intervaloInput.text.toString().toIntOrNull()
            if (valor != null && valor > 0) {
                AdminConfig.intervaloMinutos = valor
                Toast.makeText(this, "Intervalo actualizado a $valor minutos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Intervalo inválido", Toast.LENGTH_SHORT).show()
            }
        }

        btnVerLogs.setOnClickListener {
            val dbHelper = AdminDbHelper(this)
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM gpio_log ORDER BY timestamp DESC", null)

            val result = StringBuilder()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val led = cursor.getInt(cursor.getColumnIndexOrThrow("led"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_encendidos"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))

                result.append("ID: $id | LED: $led | Encendidos: $cantidad | Hora: $time\n")
            }

            cursor.close()
            textLogs.text = result.toString()
        }
    }
}
