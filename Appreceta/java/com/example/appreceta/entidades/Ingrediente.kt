// Entidad Ingrediente
package com.example.appreceta.datos.entidades

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ingredientes", indices = [Index(value = ["nombre"], unique = true)])
data class Ingrediente(
    @PrimaryKey(autoGenerate = true)
    val ingredienteId: Long = 0,
    val nombre: String
)