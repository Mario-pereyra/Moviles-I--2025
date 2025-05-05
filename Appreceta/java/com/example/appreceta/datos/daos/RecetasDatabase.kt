
package com.example.appreceta.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appreceta.datos.daos.IngredienteDao
import com.example.appreceta.datos.daos.RecetaDao
import com.example.appreceta.datos.daos.RecetaIngredienteDao
import com.example.appreceta.datos.entidades.Ingrediente
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.entidades.RecetaIngrediente

@Database(
    entities = [Receta::class, Ingrediente::class, RecetaIngrediente::class],
    version = 1,
    exportSchema = false
)
abstract class RecetasDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun ingredienteDao(): IngredienteDao
    abstract fun recetaIngredienteDao(): RecetaIngredienteDao

    companion object {
        @Volatile
        private var INSTANCIA: RecetasDatabase? = null

        fun obtenerInstancia(contexto: Context): RecetasDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    RecetasDatabase::class.java,
                    "recetas_database"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}