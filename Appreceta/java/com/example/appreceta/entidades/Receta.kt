// Entidad Receta
package com.example.appreceta.datos.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val recetaId: Long = 0,
    val nombre: String,
    val preparacion: String
)