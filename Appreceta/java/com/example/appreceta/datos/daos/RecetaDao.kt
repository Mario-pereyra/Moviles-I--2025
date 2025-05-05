package com.example.appreceta.datos.daos

import androidx.room.*
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.relaciones.RecetaConIngredientes
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReceta(receta: Receta): Long

    @Query("SELECT * FROM recetas")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>

    @Query("SELECT * FROM recetas WHERE recetaId = :recetaId")
    suspend fun obtenerRecetaPorId(recetaId: Long): Receta?

    @Transaction
    @Query("SELECT * FROM recetas WHERE recetaId = :recetaId")
    suspend fun obtenerRecetaConIngredientes(recetaId: Long): RecetaConIngredientes?

    @Query("""
        SELECT r.* FROM recetas r
        JOIN receta_ingredientes ri ON r.recetaId = ri.recetaId
        WHERE ri.ingredienteId IN (:ingredientesIds)
        GROUP BY r.recetaId
        HAVING COUNT(DISTINCT ri.ingredienteId) = :cantidadIngredientes
    """)
    suspend fun buscarRecetasConIngredientes(ingredientesIds: List<Long>, cantidadIngredientes: Int): List<Receta>

    @Query("DELETE FROM recetas WHERE recetaId = :recetaId")
    suspend fun eliminarReceta(recetaId: Long): Int
}