// ui/viewmodels/CrearRecetaViewModel.kt
package com.example.appreceta.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appreceta.datos.entidades.Ingrediente
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.repositorios.IngredienteRepository
import com.example.appreceta.datos.repositorios.RecetaRepository
import com.example.appreceta.ui.adaptadores.IngredientesRecetaAdapter.IngredienteReceta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CrearRecetaViewModel(
    private val ingredienteRepository: IngredienteRepository,
    private val recetaRepository: RecetaRepository
) : ViewModel() {

    private val _ingredientesReceta = MutableStateFlow<List<IngredienteReceta>>(emptyList())
    val ingredientesReceta: StateFlow<List<IngredienteReceta>> = _ingredientesReceta.asStateFlow()

    private val _recetaGuardada = MutableStateFlow(false)
    val recetaGuardada: StateFlow<Boolean> = _recetaGuardada.asStateFlow()

    // Caché de ingredientes
    private val _todosLosIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())

    init {
        cargarIngredientes()
    }

    private fun cargarIngredientes() {
        viewModelScope.launch {
            ingredienteRepository.obtenerTodosLosIngredientes().collect {
                _todosLosIngredientes.value = it
            }
        }
    }

    suspend fun obtenerTodosLosIngredientes(): List<Ingrediente> {
        return _todosLosIngredientes.value
    }

    fun agregarIngredienteAReceta(ingredienteId: Long, cantidad: String, unidad: String?) {
        val ingrediente = _todosLosIngredientes.value.find { it.ingredienteId == ingredienteId }
            ?: return

        val ingredienteReceta = IngredienteReceta(
            id = ingredienteId,
            nombre = ingrediente.nombre,
            cantidad = cantidad,
            unidad = unidad
        )

        val lista = _ingredientesReceta.value.toMutableList()
        lista.add(ingredienteReceta)
        _ingredientesReceta.value = lista
    }

    fun eliminarIngrediente(posicion: Int) {
        val lista = _ingredientesReceta.value.toMutableList()
        if (posicion in lista.indices) {
            lista.removeAt(posicion)
            _ingredientesReceta.value = lista
        }
    }

    fun crearNuevoIngrediente(nombre: String) {
        viewModelScope.launch {
            val id = ingredienteRepository.agregarIngrediente(nombre)
            if (id > 0) {
                // Se ha creado correctamente, se actualizará automáticamente mediante el flow
            }
        }
    }

    fun guardarReceta(nombre: String, preparacion: String) {
        viewModelScope.launch {
            val receta = Receta(
                nombre = nombre,
                preparacion = preparacion
            )

            val ingredientesConCantidad = _ingredientesReceta.value.map {
                Triple(it.id, it.cantidad, it.unidad)
            }

            val recetaId = recetaRepository.guardarRecetaCompleta(receta, ingredientesConCantidad)
            _recetaGuardada.value = recetaId > 0
        }
    }

    class Factory(
        private val ingredienteRepository: IngredienteRepository,
        private val recetaRepository: RecetaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CrearRecetaViewModel::class.java)) {
                return CrearRecetaViewModel(ingredienteRepository, recetaRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}