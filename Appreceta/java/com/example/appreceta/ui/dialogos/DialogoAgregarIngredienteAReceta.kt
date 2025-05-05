package com.example.appreceta.ui.dialogos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.appreceta.databinding.DialogoAgregarIngredienteRecetaBinding
import com.example.appreceta.datos.entidades.Ingrediente

class DialogoAgregarIngredienteAReceta(
    private val ingredientes: List<Ingrediente>,
    private val onAgregarClick: (Long, String, String?) -> Unit,
    private val onNuevoIngredienteClick: () -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogoAgregarIngredienteRecetaBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogoAgregarIngredienteRecetaBinding.inflate(LayoutInflater.from(context))

        // Configurar el spinner con los ingredientes
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            ingredientes.map { it.nombre }
        )
        binding.spinnerIngredientes.adapter = adapter

        // Configurar el botón de nuevo ingrediente
        binding.btnNuevoIngrediente.setOnClickListener {
            dismiss()
            onNuevoIngredienteClick()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Agregar ingrediente")
            .setView(binding.root)
            .setPositiveButton("Agregar") { _, _ ->
                val posicion = binding.spinnerIngredientes.selectedItemPosition
                if (posicion >= 0 && posicion < ingredientes.size) {
                    val ingrediente = ingredientes[posicion]
                    val cantidad = binding.etCantidad.text.toString().trim()
                    val unidad = binding.etUnidad.text.toString().trim().let {
                        if (it.isEmpty()) null else it
                    }
                    onAgregarClick(ingrediente.ingredienteId, cantidad, unidad)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}