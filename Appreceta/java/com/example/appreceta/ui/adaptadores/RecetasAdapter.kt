package com.example.appreceta.ui.adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appreceta.databinding.ItemRecetaBinding
import com.example.appreceta.datos.entidades.Receta

class RecetasAdapter(
    private val onRecetaClick: (Receta) -> Unit
) : ListAdapter<Receta, RecetasAdapter.RecetaViewHolder>(RecetaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val binding = ItemRecetaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecetaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecetaViewHolder(
        private val binding: ItemRecetaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(receta: Receta) {
            binding.tvNombreReceta.text = receta.nombre

            binding.root.setOnClickListener {
                onRecetaClick(receta)
            }
        }
    }

    private class RecetaDiffCallback : DiffUtil.ItemCallback<Receta>() {
        override fun areItemsTheSame(oldItem: Receta, newItem: Receta): Boolean {
            return oldItem.recetaId == newItem.recetaId
        }

        override fun areContentsTheSame(oldItem: Receta, newItem: Receta): Boolean {
            return oldItem == newItem
        }
    }
}