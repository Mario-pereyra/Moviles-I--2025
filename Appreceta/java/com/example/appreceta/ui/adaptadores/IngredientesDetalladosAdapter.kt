package com.example.appreceta.ui.adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appreceta.databinding.ItemIngredienteDetalladoBinding
import com.example.appreceta.datos.relaciones.IngredienteConDetalles

class IngredientesDetalladosAdapter : ListAdapter<IngredienteConDetalles, IngredientesDetalladosAdapter.IngredienteViewHolder>(IngredientesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val binding = ItemIngredienteDetalladoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IngredienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IngredienteViewHolder(
        private val binding: ItemIngredienteDetalladoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingrediente: IngredienteConDetalles) {
            val textoCompleto = if (ingrediente.unidad != null) {
                "${ingrediente.ingrediente.nombre}: ${ingrediente.cantidad} ${ingrediente.unidad}"
            } else {
                "${ingrediente.ingrediente.nombre}: ${ingrediente.cantidad}"
            }
            binding.tvIngredienteDetalle.text = textoCompleto
        }
    }

    private class IngredientesDiffCallback : DiffUtil.ItemCallback<IngredienteConDetalles>() {
        override fun areItemsTheSame(oldItem: IngredienteConDetalles, newItem: IngredienteConDetalles): Boolean {
            return oldItem.ingrediente.ingredienteId == newItem.ingrediente.ingredienteId
        }

        override fun areContentsTheSame(oldItem: IngredienteConDetalles, newItem: IngredienteConDetalles): Boolean {
            return oldItem == newItem
        }
    }
}