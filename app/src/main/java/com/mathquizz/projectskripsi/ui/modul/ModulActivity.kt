package com.mathquizz.projectskripsi.ui.modul

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
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
            // Handle the error if binding fails
        }

        materiId = intent.getStringExtra("materiId")
        submateriId = intent.getStringExtra("submateriId")
        title = intent.getStringExtra("title")

        if (materiId != null && submateriId != null) {
            viewModel.setModulId(materiId!!, submateriId!!)
        }
        binding.tvListModul.text = title
        setupObservers()

        if (materiId != null) {
            viewModel.getMateriTitle(materiId!!) { retrievedTitle ->
                title = retrievedTitle // Properly assign title here
                binding.btnModul.setOnClickListener {
                    val userId = auth.currentUser?.uid ?: return@setOnClickListener
                    if (materiId != null && submateriId != null) {

                        binding.progressBar.visibility = ProgressBar.VISIBLE
                        viewModel.updateUserProgress(userId, materiId!!, submateriId!!, progress) { showPopup ->
                            binding.progressBar.visibility = ProgressBar.GONE
                            if (showPopup) {
                                showPopupDialog(
                                    "Anda telah mendapatkan \n progress points: $progress%.",
                                    progress
                                ) {
                                    val intent = Intent(this, SubMateriActivity::class.java).apply {
                                        putExtra("materiId", materiId)
                                        putExtra("title", title)
                                        putExtra("progress", progress)
                                    }
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                val intent = Intent(this, SubMateriActivity::class.java).apply {
                                    putExtra("materiId", materiId)
                                    putExtra("title", title)
                                    putExtra("progress", progress)
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss() // Ensure dialog is dismissed when activity is destroyed
    }


    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.modul.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading state if needed
                    }

                    is Resource.Success -> {
                        val modulList = resource.data ?: emptyList() // Handle nullable list
                        if (modulList.isNotEmpty()) {
                            val modul = modulList[0] // Assuming you want the first module's image

                            Glide.with(this@ModulActivity)
                                .load(modul.imagemodul)
                                .into(binding.ivModul)
                            progress = modul.progress
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = ProgressBar.GONE
                    }

                    else -> {
                        binding.progressBar.visibility = ProgressBar.GONE
                    }
                }
            }
        }
    }
}