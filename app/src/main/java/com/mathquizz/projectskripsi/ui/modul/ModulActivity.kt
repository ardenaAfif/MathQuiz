package com.mathquizz.projectskripsi.ui.modul

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var dialog: AlertDialog? = null
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

        val collectionName = intent.getStringExtra("collectionName") ?: "materi"

        materiId = intent.getStringExtra("materiId")
        submateriId = intent.getStringExtra("submateriId")
        title = intent.getStringExtra("title")

        binding.tvListModul.text = title

        if (materiId != null && submateriId != null) {
            viewModel.setModulId(collectionName, materiId!!, submateriId!!)
        }

        setupObservers()
        setupButtonAction()


    }


    private fun setupButtonAction() {
        binding.btnModul.setOnClickListener {
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            if (materiId != null && submateriId != null) {
                binding.progressBar.visibility = ProgressBar.VISIBLE

                // Update Progress di Firebase
                viewModel.updateUserProgress(userId, materiId!!, submateriId!!, progress) { showPopup ->
                    binding.progressBar.visibility = ProgressBar.GONE

                    if (showPopup) {
                        showPopupDialog(
                            "Anda telah mendapatkan \n progress points: $progress%.",
                            progress
                        ) {
                            sendResultAndFinish() // Kembali ke SubMateriActivity
                        }
                    } else {
                        sendResultAndFinish() // Kembali ke SubMateriActivity
                    }
                }
            }
        }
    }

    private fun sendResultAndFinish() {
        val resultIntent = Intent().apply {
            putExtra("progress", progress)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.modul.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = ProgressBar.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = ProgressBar.GONE
                        val modulList = resource.data ?: emptyList()
                        if (modulList.isNotEmpty()) {
                            val modul = modulList[0]
                            Glide.with(this@ModulActivity)
                                .load(modul.imagemodul)
                                .into(binding.ivModul)
                            progress = modul.progress
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = ProgressBar.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss() // Ensure dialog is dismissed when activity is destroyed
    }
}