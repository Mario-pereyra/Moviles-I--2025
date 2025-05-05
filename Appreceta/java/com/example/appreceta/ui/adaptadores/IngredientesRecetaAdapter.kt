package com.example.appreceta.ui.adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appreceta.databinding.ItemIngredienteRecetaBinding
import com.example.appreceta.ui.adaptadores.IngredientesRecetaAdapter.IngredienteReceta

class IngredientesRecetaAdapter(
    private val onEliminarClick: (Int) -> Unit
) : ListAdapter<IngredienteReceta, IngredientesRecetaAdapter.ViewHolder>(IngredienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredienteRecetaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(private val binding: ItemIngredienteRecetaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IngredienteReceta, position: Int) {
            binding.tvIngredienteNombre.text = item.nombre
            binding.tvCantidadUnidad.text = if (item.unidad != null) {
                "${item.cantidad} ${item.unidad}"
            } else {
                item.cantidad
            }

            binding.btnEliminar.setOnClickListener {
                onEliminarClick(position)
            }
        }
    }

    private class IngredienteDiffCallback : DiffUtil.ItemCallback<IngredienteReceta>() {
        override fun areItemsTheSame(oldItem: IngredienteReceta, newItem: IngredienteReceta): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IngredienteReceta, newItem: IngredienteReceta): Boolean {
            return oldItem == newItem
        }
    }

    data class IngredienteReceta(
        val id: Long,
        val nombre: String,
        val cantidad: String,
        val unidad: String?
    )
}