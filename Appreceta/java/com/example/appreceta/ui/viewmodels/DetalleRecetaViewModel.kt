// ui/viewmodels/DetalleRecetaViewModel.kt
package com.example.appreceta.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appreceta.datos.daos.RecetaDao
import com.example.appreceta.datos.relaciones.IngredienteConDetalles
import com.example.appreceta.datos.relaciones.RecetaConIngredientes
import com.example.appreceta.datos.relaciones.obtenerIngredientesConDetalles
import com.example.appreceta.datos.repositorios.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleRecetaViewModel(
    private val recetaRepository: RecetaRepository,
    private val recetaId: Long
) : ViewModel() {

    // Estado para la receta y sus ingredientes
    private val _receta = MutableStateFlow<RecetaConIngredientes?>(null)
    val receta: StateFlow<RecetaConIngredientes?> = _receta.asStateFlow()

    // Estado para ingredientes procesados con sus detalles
    private val _ingredientesDetallados = MutableStateFlow<List<IngredienteConDetalles>>(emptyList())
    val ingredientesDetallados: StateFlow<List<IngredienteConDetalles>> = _ingredientesDetallados.asStateFlow()

    // Estado de carga
    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    init {
        cargarReceta()
    }

    private fun cargarReceta() {
        viewModelScope.launch {
            _estaCargando.value = true
            try {
                val recetaCompleta = recetaRepository.obtenerRecetaCompleta(recetaId)
                _receta.value = recetaCompleta

                // Procesar ingredientes para mostrar
                recetaCompleta?.let {
                    _ingredientesDetallados.value = it.obtenerIngredientesConDetalles()
                }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _estaCargando.value = false
            }
        }
    }

    private val _recetaEliminada = MutableStateFlow(false)
    val recetaEliminada: StateFlow<Boolean> = _recetaEliminada.asStateFlow()

    fun eliminarReceta() {
        viewModelScope.launch {
            val resultado = recetaRepository.eliminarReceta(recetaId)
            _recetaEliminada.value = resultado
        }
    }

    class Factory(
        private val recetaRepository: RecetaRepository,
        private val recetaId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetalleRecetaViewModel::class.java)) {
                return DetalleRecetaViewModel(recetaRepository, recetaId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}