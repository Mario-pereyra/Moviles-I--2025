package com.example.appreceta.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appreceta.datos.entidades.Ingrediente
import com.example.appreceta.datos.repositorios.IngredienteRepository
import com.example.appreceta.datos.repositorios.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IngredienteViewModel(
    private val ingredienteRepository: IngredienteRepository,
    private val recetaRepository: RecetaRepository
) : ViewModel() {

    // Lista de todos los ingredientes disponibles
    private val _ingredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientes: StateFlow<List<Ingrediente>> = _ingredientes.asStateFlow()

    // Lista de IDs de ingredientes seleccionados
    private val _ingredientesSeleccionados = MutableStateFlow<Set<Long>>(emptySet())
    val ingredientesSeleccionados: StateFlow<Set<Long>> = _ingredientesSeleccionados.asStateFlow()

    // Estado de creación de ingredientes
    private val _estadoCreacion = MutableStateFlow<EstadoCreacionIngrediente>(EstadoCreacionIngrediente.Inactivo)
    val estadoCreacion: StateFlow<EstadoCreacionIngrediente> = _estadoCreacion.asStateFlow()

    init {
        cargarIngredientes()
    }

    private fun cargarIngredientes() {
        viewModelScope.launch {
            ingredienteRepository.obtenerTodosLosIngredientes().collect { listaIngredientes ->
                _ingredientes.value = listaIngredientes
            }
        }
    }

    fun seleccionarIngrediente(ingredienteId: Long) {
        val seleccionActual = _ingredientesSeleccionados.value.toMutableSet()
        if (seleccionActual.contains(ingredienteId)) {
            seleccionActual.remove(ingredienteId)
        } else {
            seleccionActual.add(ingredienteId)
        }
        _ingredientesSeleccionados.value = seleccionActual
    }

    fun limpiarSeleccion() {
        _ingredientesSeleccionados.value = emptySet()
    }

    fun agregarNuevoIngrediente(nombre: String) {
        if (nombre.isBlank()) {
            _estadoCreacion.value = EstadoCreacionIngrediente.Error("El nombre no puede estar vacío")
            return
        }

        viewModelScope.launch {

            try {
                val resultado = ingredienteRepository.agregarIngrediente(nombre)
                if (resultado > 0) {
                    _estadoCreacion.value = EstadoCreacionIngrediente.Exito
                } else {
                    _estadoCreacion.value = EstadoCreacionIngrediente.Error("El ingrediente ya existe")
                }
            } catch (e: Exception) {
                _estadoCreacion.value = EstadoCreacionIngrediente.Error("Error al crear: ${e.message}")
            }

        }
    }

    fun reiniciarEstadoCreacion() {
        _estadoCreacion.value = EstadoCreacionIngrediente.Inactivo
    }

    // Clases selladas para manejar el estado de creación
    sealed class EstadoCreacionIngrediente {
        object Inactivo : EstadoCreacionIngrediente()
        object Exito : EstadoCreacionIngrediente()
        data class Error(val mensaje: String) : EstadoCreacionIngrediente()
    }

    // Factory para crear el ViewModel con dependencias
    class Factory(
        private val ingredienteRepository: IngredienteRepository,
        private val recetaRepository: RecetaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IngredienteViewModel::class.java)) {
                return IngredienteViewModel(ingredienteRepository, recetaRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}