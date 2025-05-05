package com.example.appreceta.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appreceta.databinding.ActivityResultadosBinding
import com.example.appreceta.datos.RecetasDatabase
import com.example.appreceta.datos.entidades.Receta
import com.example.appreceta.datos.repositorios.RecetaRepository
import com.example.appreceta.ui.adaptadores.RecetasAdapter
import com.example.appreceta.ui.viewmodels.ResultadosViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ResultadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadosBinding
    private lateinit var viewModel: ResultadosViewModel
    private lateinit var adapter: RecetasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener los IDs de ingredientes seleccionados
        val ingredientesSeleccionados = intent.getLongArrayExtra(EXTRA_INGREDIENTES_IDS)?.toSet() ?: emptySet()

        // Configurar RecyclerView y adaptador
        adapter = RecetasAdapter { receta ->
            abrirDetalleReceta(receta)
        }
        binding.rvRecetas.layoutManager = LinearLayoutManager(this)
        binding.rvRecetas.adapter = adapter

        // Inicializar repositorio
        val database = RecetasDatabase.obtenerInstancia(this)
        val recetaRepository = RecetaRepository(database.recetaDao(), database.recetaIngredienteDao())

        // Configurar ViewModel
        viewModel = ViewModelProvider(
            this,
            ResultadosViewModel.Factory(recetaRepository, ingredientesSeleccionados)
        )[ResultadosViewModel::class.java]
        binding.btnAnadirReceta.setOnClickListener {
            CrearRecetaActivity.iniciar(this)
        }
        // Observar cambios en el estado
        observarEstadoUI()
    }

    private fun observarEstadoUI() {
        // Observar estado de carga
        lifecycleScope.launch {
            viewModel.estaCargando.collectLatest { estaCargando ->
                binding.progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
            }
        }

        // Observar resultados
        lifecycleScope.launch {
            viewModel.recetas.collectLatest { recetas ->
                adapter.submitList(recetas)

                // Mostrar mensaje si no hay resultados
                if (recetas.isEmpty() && !viewModel.estaCargando.value) {
                    binding.tvNoResultados.visibility = View.VISIBLE
                    binding.rvRecetas.visibility = View.GONE
                } else {
                    binding.tvNoResultados.visibility = View.GONE
                    binding.rvRecetas.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun abrirDetalleReceta(receta: Receta) {
        // Implementaremos esto más adelante
        // Intent hacia la actividad de detalle
        DetalleRecetaActivity.iniciar(this, receta.recetaId)
    }

    companion object {
        private const val EXTRA_INGREDIENTES_IDS = "extra_ingredientes_ids"

        fun iniciar(context: Context, ingredientesIds: Set<Long>) {
            val intent = Intent(context, ResultadosActivity::class.java).apply {
                putExtra(EXTRA_INGREDIENTES_IDS, ingredientesIds.toLongArray())
            }
            context.startActivity(intent)
        }
    }
}