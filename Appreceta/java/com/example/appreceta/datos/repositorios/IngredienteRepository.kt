package com.example.appreceta.datos.repositorios

import com.example.appreceta.datos.daos.IngredienteDao
import com.example.appreceta.datos.entidades.Ingrediente
import kotlinx.coroutines.flow.Flow

class IngredienteRepository(private val ingredienteDao: IngredienteDao) {
    // Obtener todos los ingredientes
    fun obtenerTodosLosIngredientes(): Flow<List<Ingrediente>> {
        return ingredienteDao.obtenerTodosLosIngredientes()
    }

    // Añadir nuevo ingrediente (devuelve el ID o -1 si ya existe)
    suspend fun agregarIngrediente(nombre: String): Long {
        if (nombre.isBlank()) return -1

        // Verificar si ya existe
        if (ingredienteDao.existeIngredienteConNombre(nombre)) {
            return -1
        }

        // Insertar nuevo ingrediente
        return ingredienteDao.insertarIngrediente(Ingrediente(nombre = nombre))
    }

    // Verificar si un ingrediente existe
    suspend fun existeIngrediente(nombre: String): Boolean {
        return ingredienteDao.existeIngredienteConNombre(nombre)
    }
}