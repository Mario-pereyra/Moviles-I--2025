package com.example.appreceta.datos.repositorios

import com.example.appreceta.datos.daos.RecetaDao
import com.example.appreceta.datos.daos.RecetaIngredienteDao
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.entidades.RecetaIngrediente
import com.example.appreceta.datos.relaciones.RecetaConIngredientes
import kotlinx.coroutines.flow.Flow

class RecetaRepository(
    private val recetaDao: RecetaDao,
    private val recetaIngredienteDao: RecetaIngredienteDao
) {
    // Obtener todas las recetas
    fun obtenerTodasLasRecetas(): Flow<List<Receta>> {
        return recetaDao.obtenerTodasLasRecetas()
    }

    // Buscar recetas por ingredientes seleccionados
    suspend fun buscarRecetasPorIngredientes(ingredientesIds: List<Long>): List<Receta> {
        if (ingredientesIds.isEmpty()) return emptyList()
        return recetaDao.buscarRecetasConIngredientes(
            ingredientesIds = ingredientesIds,
            cantidadIngredientes = ingredientesIds.size
        )
    }

    // Obtener una receta con sus ingredientes
    suspend fun obtenerRecetaCompleta(recetaId: Long): RecetaConIngredientes? {
        return recetaDao.obtenerRecetaConIngredientes(recetaId)
    }

    // Guardar una nueva receta con sus ingredientes
    suspend fun guardarRecetaCompleta(
        receta: Receta,
        ingredientesConCantidad: List<Triple<Long, String, String?>>
    ): Long {
        // 1. Insertar la receta y obtener su ID
        val recetaId = recetaDao.insertarReceta(receta)

        // 2. Crear y guardar las relaciones receta-ingrediente
        val recetaIngredientes = ingredientesConCantidad.map { (ingredienteId, cantidad, unidad) ->
            RecetaIngrediente(
                recetaId = recetaId,
                ingredienteId = ingredienteId,
                cantidad = cantidad,
                unidad = unidad
            )
        }
        recetaIngredienteDao.insertarRecetaIngredientes(recetaIngredientes)

        return recetaId
    }
    suspend fun eliminarReceta(recetaId: Long): Boolean {
        return recetaDao.eliminarReceta(recetaId) > 0
    }
}