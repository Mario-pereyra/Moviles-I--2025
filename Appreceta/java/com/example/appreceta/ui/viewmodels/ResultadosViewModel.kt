// ui/viewmodels/ResultadosViewModel.kt
package com.example.appreceta.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.repositorios.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultadosViewModel(
    private val recetaRepository: RecetaRepository,
    private val ingredientesSeleccionadosIds: Set<Long>
) : ViewModel() {

    // Estado para las recetas encontradas
    private val _recetas = MutableStateFlow<List<Receta>>(emptyList())
    val recetas: StateFlow<List<Receta>> = _recetas.asStateFlow()

    // Estado de carga
    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    init {
        buscarRecetas()
    }

    private fun buscarRecetas() {
        viewModelScope.launch {
            _estaCargando.value = true
            val resultados = recetaRepository.buscarRecetasPorIngredientes(
                ingredientesSeleccionadosIds.toList()
            )
            _recetas.value = resultados
            _estaCargando.value = false
        }
    }

    class Factory(
        private val recetaRepository: RecetaRepository,
        private val ingredientesSeleccionadosIds: Set<Long>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ResultadosViewModel::class.java)) {
                return ResultadosViewModel(recetaRepository, ingredientesSeleccionadosIds) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}