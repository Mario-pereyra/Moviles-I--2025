package com.example.appreceta.ui.adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appreceta.R
import com.example.appreceta.databinding.ItemIngredienteBinding
import com.example.appreceta.datos.entidades.Ingrediente

class IngredientesAdapter(
    private val onIngredienteSeleccionado: (Ingrediente) -> Unit
) : ListAdapter<Ingrediente, IngredientesAdapter.IngredienteViewHolder>(IngredienteDiffCallback()) {

    private val ingredientesSeleccionados = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val binding = ItemIngredienteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IngredienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val ingrediente = getItem(position)
        holder.bind(ingrediente, ingredientesSeleccionados.contains(ingrediente.ingredienteId))
    }

    fun toggleSeleccion(ingredienteId: Long): Boolean {
        return if (ingredientesSeleccionados.contains(ingredienteId)) {
            ingredientesSeleccionados.remove(ingredienteId)
            false
        } else {
            ingredientesSeleccionados.add(ingredienteId)
            true
        }
    }

    fun obtenerSeleccionados(): Set<Long> = ingredientesSeleccionados.toSet()

    fun limpiarSeleccion() {
        ingredientesSeleccionados.clear()
        notifyDataSetChanged()
    }

    inner class IngredienteViewHolder(
        private val binding: ItemIngredienteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingrediente: Ingrediente, seleccionado: Boolean) {
            binding.tvNombreIngrediente.text = ingrediente.nombre

            // Cambiar el color según esté seleccionado o no
            val colorFondo = if (seleccionado) {
                R.color.colorSeleccionado
            } else {
                R.color.colorNoSeleccionado
            }

            binding.cardIngrediente.setCardBackgroundColor(
                ContextCompat.getColor(binding.root.context, colorFondo)
            )

            binding.root.setOnClickListener {
                val nuevoEstado = toggleSeleccion(ingrediente.ingredienteId)
                binding.cardIngrediente.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        if (nuevoEstado) R.color.colorSeleccionado else R.color.colorNoSeleccionado
                    )
                )
                onIngredienteSeleccionado(ingrediente)
            }
        }
    }

    private class IngredienteDiffCallback : DiffUtil.ItemCallback<Ingrediente>() {
        override fun areItemsTheSame(oldItem: Ingrediente, newItem: Ingrediente): Boolean {
            return oldItem.ingredienteId == newItem.ingredienteId
        }

        override fun areContentsTheSame(oldItem: Ingrediente, newItem: Ingrediente): Boolean {
            return oldItem == newItem
        }
    }
}