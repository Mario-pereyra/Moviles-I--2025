package com.example.appreceta.datos.daos

import androidx.room.*
import com.example.appreceta.datos.entidades.RecetaIngrediente

@Dao
interface RecetaIngredienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRecetaIngrediente(recetaIngrediente: RecetaIngrediente)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRecetaIngredientes(recetaIngredientes: List<RecetaIngrediente>)
}