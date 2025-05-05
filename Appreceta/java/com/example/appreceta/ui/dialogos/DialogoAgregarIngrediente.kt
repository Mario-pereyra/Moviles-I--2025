package com.example.appreceta.ui.dialogos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.appreceta.R

class DialogoAgregarIngrediente(
    private val onGuardarClick: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialogo_agregar_ingrediente, null)

        val etNombreIngrediente = view.findViewById<EditText>(R.id.etNombreIngrediente)

        return AlertDialog.Builder(requireContext())
            .setTitle("Añadir nuevo ingrediente")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombreIngrediente.text.toString().trim()
                if (nombre.isNotEmpty()) {
                    onGuardarClick(nombre)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}