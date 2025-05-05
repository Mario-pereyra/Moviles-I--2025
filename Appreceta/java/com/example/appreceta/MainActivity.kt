package com.example.appreceta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appreceta.databinding.ActivityMainBinding
import com.example.appreceta.datos.RecetasDatabase
import com.example.appreceta.datos.repositorios.IngredienteRepository
import com.example.appreceta.datos.repositorios.RecetaRepository
import com.example.appreceta.ui.ResultadosActivity
import com.example.appreceta.ui.adaptadores.IngredientesAdapter
import com.example.appreceta.ui.dialogos.DialogoAgregarIngrediente
import com.example.appreceta.ui.viewmodels.IngredienteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: IngredienteViewModel
    private lateinit var adapter: IngredientesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el adaptador
        adapter = IngredientesAdapter { ingrediente ->
            viewModel.seleccionarIngrediente(ingrediente.ingredienteId)
        }

        // Configurar RecyclerView
        binding.rvIngredientes.layoutManager = GridLayoutManager(this, 2)
        binding.rvIngredientes.adapter = adapter

        // Inicializar la base de datos y repositorios
        val database = RecetasDatabase.obtenerInstancia(this)
        val ingredienteRepository = IngredienteRepository(database.ingredienteDao())
        val recetaRepository = RecetaRepository(database.recetaDao(), database.recetaIngredienteDao())

        // Inicializar ViewModel
        viewModel = ViewModelProvider(
            this,
            IngredienteViewModel.Factory(ingredienteRepository, recetaRepository)
        )[IngredienteViewModel::class.java]

        // Observar cambios en los ingredientes
        lifecycleScope.launch {
            viewModel.ingredientes.collectLatest { ingredientes ->
                adapter.submitList(ingredientes)
            }
        }

        // Observar cambios en la selección
        lifecycleScope.launch {
            viewModel.ingredientesSeleccionados.collectLatest { seleccionados ->
                binding.btnBuscarRecetas.isEnabled = seleccionados.isNotEmpty()
            }
        }

        // Configurar botones
        binding.btnAgregarIngrediente.setOnClickListener {
            mostrarDialogoAgregarIngrediente()
        }

        binding.btnBuscarRecetas.setOnClickListener {
            buscarRecetasConIngredientes()
        }
    }

    private fun mostrarDialogoAgregarIngrediente() {
        // Implementaremos este diálogo a continuación
        DialogoAgregarIngrediente { nombre ->
            viewModel.agregarNuevoIngrediente(nombre)
        }.show(supportFragmentManager, "dialogoAgregarIngrediente")
    }

    private fun buscarRecetasConIngredientes() {
        val ingredientesSeleccionados = viewModel.ingredientesSeleccionados.value
        if (ingredientesSeleccionados.isNotEmpty()) {
            ResultadosActivity.iniciar(this, ingredientesSeleccionados)
        }
    }
}