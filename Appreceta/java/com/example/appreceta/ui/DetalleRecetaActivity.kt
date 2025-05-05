package com.example.appreceta.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appreceta.databinding.ActivityDetalleRecetaBinding
import com.example.appreceta.datos.RecetasDatabase
import com.example.appreceta.datos.repositorios.RecetaRepository
import com.example.appreceta.ui.adaptadores.IngredientesDetalladosAdapter
import com.example.appreceta.ui.viewmodels.DetalleRecetaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetalleRecetaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleRecetaBinding
    private lateinit var viewModel: DetalleRecetaViewModel
    private lateinit var adapter: IngredientesDetalladosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle de Receta"

        // Obtener el ID de la receta
        val recetaId = intent.getLongExtra(EXTRA_RECETA_ID, -1)
        if (recetaId == -1L) {
            finish()
            return
        }

        // Inicializar adaptador
        adapter = IngredientesDetalladosAdapter()
        binding.rvIngredientes.layoutManager = LinearLayoutManager(this)
        binding.rvIngredientes.adapter = adapter

        // Inicializar ViewModel
        val database = RecetasDatabase.obtenerInstancia(this)
        val recetaRepository =
            RecetaRepository(database.recetaDao(), database.recetaIngredienteDao())
        viewModel = ViewModelProvider(
            this,
            DetalleRecetaViewModel.Factory(recetaRepository, recetaId)
        )[DetalleRecetaViewModel::class.java]

        // Configurar el botón de eliminar
        binding.btnEliminarReceta.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        // Observar datos
        observarDatos()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar receta")
            .setMessage("¿Estás seguro de que deseas eliminar esta receta?")
            .setPositiveButton("Eliminar") { dialog, which ->
                viewModel.eliminarReceta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observarDatos() {
        // Observar estado de carga
        lifecycleScope.launch {
            viewModel.estaCargando.collectLatest { estaCargando ->
                binding.progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
            }
        }

        // Observar la receta
        lifecycleScope.launch {
            viewModel.receta.collectLatest { receta ->
                receta?.let {
                    binding.tvNombreReceta.text = it.receta.nombre
                    binding.tvPreparacion.text = it.receta.preparacion
                }
            }
        }

        // Observar ingredientes detallados
        lifecycleScope.launch {
            viewModel.ingredientesDetallados.collectLatest { ingredientes ->
                adapter.submitList(ingredientes)
            }
        }

        // Observar si la receta fue eliminada
        lifecycleScope.launch {
            viewModel.recetaEliminada.collectLatest { eliminada ->
                if (eliminada) {
                    Toast.makeText(
                        this@DetalleRecetaActivity,
                        "Receta eliminada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    companion object {
        private const val EXTRA_RECETA_ID = "extra_receta_id"

        fun iniciar(context: Context, recetaId: Long) {
            val intent = Intent(context, DetalleRecetaActivity::class.java).apply {
                putExtra(EXTRA_RECETA_ID, recetaId)
            }
            context.startActivity(intent)
        }
    }
}