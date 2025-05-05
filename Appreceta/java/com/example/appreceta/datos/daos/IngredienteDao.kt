package com.example.appreceta.datos.daos

import androidx.room.*
import com.example.appreceta.datos.entidades.Ingrediente
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredienteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarIngrediente(ingrediente: Ingrediente): Long

    @Query("SELECT * FROM ingredientes ORDER BY nombre")
    fun obtenerTodosLosIngredientes(): Flow<List<Ingrediente>>

    @Query("SELECT * FROM ingredientes WHERE ingredienteId = :ingredienteId")
    suspend fun obtenerIngredientePorId(ingredienteId: Long): Ingrediente?

    @Query("SELECT EXISTS(SELECT 1 FROM ingredientes WHERE nombre = :nombre LIMIT 1)")
    suspend fun existeIngredienteConNombre(nombre: String): Boolean
}