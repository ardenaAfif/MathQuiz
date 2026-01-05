package com.mathquizz.projectskripsi.ui.materi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.adapter.MateriAdapter
import com.mathquizz.projectskripsi.databinding.ActivityMateriBinding
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding
    private val viewModel: MateriViewModel by viewModels()
    private lateinit var materiAdapter: MateriAdapter

    private var currentCollectionName: String = "materi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val collectionName = intent.getStringExtra("COLLECTION_NAME") ?: "materi"

        currentCollectionName = collectionName

        binding.tvTitle.text = if(collectionName == "materiintegral") "Materi Integral" else "Materi Turunan"

        setupRvMateri()
        observeMateri()

        // Fetch data
        viewModel.fetchMateriList(collectionName)
    }

    private fun setupRvMateri() {
        materiAdapter = MateriAdapter(this) { materiId, title ->
            navigateToSubMateri(materiId, title)
        }

        binding.rvMateriList.apply {
            layoutManager = LinearLayoutManager(this@MateriActivity)
            adapter = materiAdapter
        }
    }

    private fun navigateToSubMateri(materiId: String, title: String) {
        val intent = Intent(this, SubMateriActivity::class.java)

        // Setup default config
        var adapterType = 1
        var dialogType = 1

        /*
        if (materiId == "id_materi_susah" || title.contains("Lanjutan")) {
            adapterType = 2
            dialogType = 2
        } else if (title.contains("Quiz")) {
            adapterType = 2
            dialogType = 3
        }
        */

        intent.putExtra("materiId", materiId)
        intent.putExtra("title", title)
        intent.putExtra("collectionName", currentCollectionName)
        intent.putExtra("adapterType", adapterType)
        intent.putExtra("dialogType", dialogType)

        startActivity(intent)
        Log.d("MateriActivity", "Navigasi ke SubMateri: $materiId | Collection: $currentCollectionName")
    }

    private fun observeMateri() {
        lifecycleScope.launchWhenStarted {
            viewModel.materiList.collectLatest { resource ->
                when(resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        materiAdapter.differ.submitList(resource.data)
                        viewModel.checkClickability()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MateriActivity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.clickableItems.collect { clickableItems ->
                materiAdapter.updateClickableItems(clickableItems)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.materiList.value is Resource.Success){
            viewModel.fetchMateriList(currentCollectionName)
        }
    }
}