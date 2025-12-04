package com.mathquizz.projectskripsi.ui.submateri

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.adapter.SubMateriAdapter2
import com.mathquizz.projectskripsi.databinding.ActivitySubMateriBinding
import com.mathquizz.projectskripsi.dialog.showDialogComplete
import com.mathquizz.projectskripsi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SubMateriActivity4 : AppCompatActivity() {

    private lateinit var binding: ActivitySubMateriBinding
    private lateinit var subMateriAdapter: SubMateriAdapter2
    private val viewModel by viewModels<SubMateriViewModel>()
    private var hasShownCongratsDialog = false


    private val startModulActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val progressIncrement = result.data?.getIntExtra("progress", 0) ?: 0
                updateProgress(progressIncrement)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "SubMateriActivity4 dipanggil")   //Cek Pemanggilan

        binding = ActivitySubMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)


        val materiId = intent.getStringExtra("materiId")
        val title = intent.getStringExtra("title")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        materiId?.let {
            viewModel.setMateriId(it)
            userId?.let { userId ->
                viewModel.setUserId(userId)

            }
            setupRvSubMateri(it)
        }
        binding.tvList.text = title
        submateriSetup()
        lifecycleScope.launchWhenStarted {
            viewModel.progress.collectLatest { progress ->
                updateProgress(progress)
                if (progress >= 80 && !hasShownCongratsDialog) {
                    hasShownCongratsDialog = true
                    showDialogComplete(
                        onYesClicked = {
                            // Handle Yes button click
                        }
                    )
                }
            }
        }

    }

    private fun updateProgress(increment: Int) {

        val newProgress = (increment).coerceAtMost(100) // Ensure progress does not exceed 100%
        binding.scoreProgressIndicator.progress = newProgress
        binding.scoreProgressText.text = "$newProgress%"
        subMateriAdapter.isProgressSufficient = newProgress >= 50

    }

    private fun submateriSetup() {
        val materiId = intent.getStringExtra("materiId") ?: return
        subMateriAdapter = SubMateriAdapter2(this, materiId, startModulActivityLauncher, isProgressSufficient = false)
        binding.rvSubMateri.apply {
            layoutManager = LinearLayoutManager(this@SubMateriActivity4, LinearLayoutManager.VERTICAL, false)
            adapter = subMateriAdapter

        }

    }

    private fun setupRvSubMateri(materiId: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.subMateriList.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        subMateriAdapter.differ.submitList(result.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Sub Materi List", result.message.toString())
                        Toast.makeText(this@SubMateriActivity4, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateToHome", true)


        }
        startActivity(intent)
        finishAffinity()// Finish the current activity so it's removed from the back stack
    }
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }



}