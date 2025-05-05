package com.example.appreceta.datos.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.appreceta.datos.entidades.Ingrediente
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.entidades.RecetaIngrediente

// Clase que representa una receta con sus ingredientes
data class RecetaConIngredientes(
    @Embedded val receta: Receta,

    // Esto obtiene los ingredientes básicos sin cantidades
    @Relation(
        parentColumn = "recetaId",
        entityColumn = "ingredienteId",
        associateBy = Junction(
            value = RecetaIngrediente::class,
            parentColumn = "recetaId",
            entityColumn = "ingredienteId"
        )
    )
    val ingredientes: List<Ingrediente>,

    // Esto obtiene las relaciones con cantidades
    @Relation(
        parentColumn = "recetaId",
        entityColumn = "recetaId", // Mismo valor porque es una relación directa
        entity = RecetaIngrediente::class
    )
    val recetaIngredientes: List<RecetaIngrediente>
)

// Clase auxiliar para presentación en la UI
data class IngredienteConDetalles(
    val ingrediente: Ingrediente,
    val cantidad: String,
    val unidad: String?
)

// Extensión para transformar los datos a un formato más útil
fun RecetaConIngredientes.obtenerIngredientesConDetalles(): List<IngredienteConDetalles> {
    val mapIngredientes = ingredientes.associateBy { it.ingredienteId }

    return recetaIngredientes.mapNotNull { recetaIng ->
        mapIngredientes[recetaIng.ingredienteId]?.let { ingrediente ->
            IngredienteConDetalles(
                ingrediente = ingrediente,
                cantidad = recetaIng.cantidad,
                unidad = recetaIng.unidad
            )
        }
    }
}