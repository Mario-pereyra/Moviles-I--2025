package com.example.appreceta.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "receta_ingredientes",
    primaryKeys = ["recetaId", "ingredienteId"],
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["recetaId"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingrediente::class,
            parentColumns = ["ingredienteId"],
            childColumns = ["ingredienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecetaIngrediente(
    val recetaId: Long,
    val ingredienteId: Long,
    val cantidad: String,
    val unidad: String?
)