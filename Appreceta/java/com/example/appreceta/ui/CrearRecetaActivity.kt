package com.example.appreceta.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appreceta.databinding.ActivityCrearRecetaBinding
import com.example.appreceta.datos.RecetasDatabase
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.repositorios.IngredienteRepository
import com.example.appreceta.datos.repositorios.RecetaRepository
import com.example.appreceta.ui.adaptadores.IngredientesRecetaAdapter
import com.example.appreceta.ui.dialogos.DialogoAgregarIngrediente
import com.example.appreceta.ui.dialogos.DialogoAgregarIngredienteAReceta
import com.example.appreceta.ui.viewmodels.CrearRecetaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CrearRecetaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearRecetaBinding
    private lateinit var viewModel: CrearRecetaViewModel
    private lateinit var adapter: IngredientesRecetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Crear Nueva Receta"

        // Inicializar adaptador
        adapter = IngredientesRecetaAdapter { posicion ->
            viewModel.eliminarIngrediente(posicion)
        }
        binding.rvIngredientesReceta.layoutManager = LinearLayoutManager(this)
        binding.rvIngredientesReceta.adapter = adapter

        // Inicializar ViewModel
        val database = RecetasDatabase.obtenerInstancia(this)
        val ingredienteRepository = IngredienteRepository(database.ingredienteDao())
        val recetaRepository = RecetaRepository(database.recetaDao(), database.recetaIngredienteDao())

        val factory = CrearRecetaViewModel.Factory(ingredienteRepository, recetaRepository)
        viewModel = ViewModelProvider(this, factory)[CrearRecetaViewModel::class.java]

        // Configurar botones
        binding.btnAgregarIngrediente.setOnClickListener {
            mostrarDialogoAgregarIngredienteAReceta()
        }

        binding.btnGuardarReceta.setOnClickListener {
            guardarReceta()
        }

        // Observar cambios
        observarCambios()
    }

    private fun observarCambios() {
        lifecycleScope.launch {
            viewModel.ingredientesReceta.collectLatest {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.recetaGuardada.collectLatest { guardadaExitosamente ->
                if (guardadaExitosamente) {
                    Toast.makeText(this@CrearRecetaActivity,
                        "Receta guardada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun mostrarDialogoAgregarIngredienteAReceta() {
        lifecycleScope.launch {
            val ingredientes = viewModel.obtenerTodosLosIngredientes()
            DialogoAgregarIngredienteAReceta(
                ingredientes = ingredientes,
                onAgregarClick = { ingredienteId, cantidad, unidad ->
                    viewModel.agregarIngredienteAReceta(ingredienteId, cantidad, unidad)
                },
                onNuevoIngredienteClick = {
                    mostrarDialogoNuevoIngrediente()
                }
            ).show(supportFragmentManager, "dialogoAgregarIngredienteReceta")
        }
    }

    private fun mostrarDialogoNuevoIngrediente() {
        DialogoAgregarIngrediente { nombre ->
            viewModel.crearNuevoIngrediente(nombre)
        }.show(supportFragmentManager, "dialogoNuevoIngrediente")
    }

    private fun guardarReceta() {
        val nombre = binding.etNombreReceta.text.toString().trim()
        val preparacion = binding.etPreparacion.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la receta es obligatorio",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (viewModel.ingredientesReceta.value.isEmpty()) {
            Toast.makeText(this, "Debes agregar al menos un ingrediente",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (preparacion.isEmpty()) {
            Toast.makeText(this, "La preparación es obligatoria",
                Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.guardarReceta(nombre, preparacion)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun iniciar(context: Context) {
            val intent = Intent(context, CrearRecetaActivity::class.java)
            context.startActivity(intent)
        }
    }
}