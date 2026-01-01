package com.mathquizz.projectskripsi.ui.modul

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.mathquizz.projectskripsi.databinding.ActivityModulBinding
import com.mathquizz.projectskripsi.dialog.showPopupDialog
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity
import com.mathquizz.projectskripsi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.mathquizz.projectskripsi.ui.materi.MateriActivity
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModulActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModulBinding
    private val viewModel by viewModels<ModulViewModel>()
    private var progress: Int = 0

    private var moduleProgressWeight: Int? = null

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var materiId: String? = null
    private var title: String? = null
    private var submateriId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "ModulActivity dipanggil")   //Cek Pemanggilan

        try {
            binding = ActivityModulBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            Log.e("ModulActivity", "Error initializing binding", e)
            finish()
            return
        }

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

        binding.btnModul.isEnabled = false
        binding.btnModul.alpha = 0.5f

        materiId = intent.getStringExtra("materiId")
        submateriId = intent.getStringExtra("submateriId")

//        val collectionName = intent.getStringExtra("collectionName") ?: "materi"

        title = intent.getStringExtra("title")

        binding.tvListModul.text = title

        if (materiId != null && submateriId != null) {
            val collectionName = intent.getStringExtra("collectionName") ?: "materi"
            viewModel.setModulId(collectionName, materiId!!, submateriId!!)
        }

        setupObservers()
        setupButtonAction(materiId, submateriId)

    }


    private fun setupButtonAction(materiId: String?, submateriId: String?) {
        binding.btnModul.setOnClickListener {
            val userId = auth.currentUser?.uid ?: return@setOnClickListener
            val weight = moduleProgressWeight

            if (materiId != null && submateriId != null && weight != null) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnModul.isEnabled = false // Cegah double click

                viewModel.updateUserProgress(userId, materiId, submateriId, weight) { showPopup ->
                    binding.progressBar.visibility = View.GONE

                    if (showPopup) {
                        showPopupDialog(
                            "Anda telah mendapatkan \n progress points: $weight%.",
                            weight
                        ) {
                            finishActivityWithSuccess()
                        }
                    } else {
                        finishActivityWithSuccess()
                    }
                }
            } else {
                Toast.makeText(this, "Data modul belum siap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun finishActivityWithSuccess() {
        setResult(RESULT_OK)
        finish()
    }

    private fun sendResultAndFinish() {
        val resultIntent = Intent().apply {
            putExtra("progress", progress)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.modul.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnModul.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val modulList = resource.data ?: emptyList()

                            if (modulList.isNotEmpty()) {
                                val modul = modulList[0]
                                Glide.with(this@ModulActivity)
                                    .load(modul.imagemodul)
                                    .into(binding.ivModul)

                                // Simpan bobot nilai modul ini
                                moduleProgressWeight = modul.progress

                                // Aktifkan tombol karena data sudah ada
                                binding.btnModul.isEnabled = true
                                binding.btnModul.alpha = 1.0f
                            }
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@ModulActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}