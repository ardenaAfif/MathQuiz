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
import androidx.recyclerview.widget.RecyclerView
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.adapter.SubMateriAdapter
import com.mathquizz.projectskripsi.databinding.ActivitySubMateriBinding
import com.mathquizz.projectskripsi.dialog.setupCongratsDialog
import com.mathquizz.projectskripsi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.mathquizz.projectskripsi.dialog.showDialogComplete
import com.mathquizz.projectskripsi.dialog.showLastDialog
import com.mathquizz.projectskripsi.ui.materi.MateriActivity
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SubMateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubMateriBinding

    private lateinit var currentAdapter: RecyclerView.Adapter<*>
    private lateinit var subMateriAdapter: SubMateriAdapter

    private val viewModel by viewModels<SubMateriViewModel>()
    private var hasShownCongratsDialog = false

    private var adapterType = 1
    private var dialogType = 1
    private var collectionName = "materi"

    private val startModulActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val progressIncrement = result.data?.getIntExtra("progress", 0) ?: 0
                updateProgress(progressIncrement)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "SubMateriActivity dipanggil")

        binding = ActivitySubMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

        val materiId = intent.getStringExtra("materiId")
        val title = intent.getStringExtra("title")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        adapterType = intent.getIntExtra("adapterType", 1)
        dialogType = intent.getIntExtra("dialogType", 1)
        collectionName = intent.getStringExtra("collectionName") ?: "materi"

        materiId?.let {
            viewModel.setMateriId(collectionName, it)
            userId?.let { uid -> viewModel.setUserId(uid) }
        }
        binding.tvList.text = title

        setupAdapter(materiId ?: "")
        setupObserver()

    }

    private fun setupAdapter(materiId: String) {
        subMateriAdapter = SubMateriAdapter(
            context = this,
            materiId = materiId,
            collectionName = collectionName,
            adapterType = adapterType,
            startModulActivityLauncher = startModulActivityLauncher,
            isProgressSufficient = false
        )

        binding.rvSubMateri.apply {
            layoutManager = LinearLayoutManager(this@SubMateriActivity)
            adapter = subMateriAdapter
        }
    }

    private fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.subMateriList.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        subMateriAdapter.differ.submitList(result.data)
                        // Trigger update progress awal
                        updateProgress(viewModel.progress.value)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@SubMateriActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.progress.collectLatest { progress ->
                updateProgress(progress)
                showCompletionDialog(progress)
            }
        }
    }

    private fun showCompletionDialog(progress: Int) {
        if (progress >= 80 && !hasShownCongratsDialog) {
            hasShownCongratsDialog = true

            when (dialogType) {
                1 -> {
                    setupCongratsDialog(
                        message = "Belajar lagi atau Lanjut Materi",
                        progress = viewModel.progress.value,
                        onYesClicked = { /* Handle click */ }
                    )
                }

                2 -> { // Tipe Activity 2 & 3
                    showLastDialog(
                        message = "Ingin Belajar Kembali?",
                        onYesClicked = { /* Handle click */ }
                    )
                }

                3 -> { // Tipe Activity 4
                    showDialogComplete(
                        onYesClicked = { /* Handle click */ }
                    )
                }
            }
        }
    }

    private fun updateProgress(increment: Int) {
        val newProgress = (increment).coerceAtMost(100)
        binding.scoreProgressIndicator.progress = newProgress
        binding.scoreProgressText.text = "$newProgress%"

        // Update clickability pada adapter yang aktif
        if (::subMateriAdapter.isInitialized) {
            subMateriAdapter.isProgressSufficient = newProgress >= 50
            subMateriAdapter.notifyDataSetChanged()
        }
    }

//    private fun submateriSetup() {
//        val materiId = intent.getStringExtra("materiId") ?: return
//        subMateriAdapter = SubMateriAdapter(this, this, materiId, startModulActivityLauncher, isProgressSufficient = false)
//        binding.rvSubMateri.apply {
//            layoutManager = LinearLayoutManager(this@SubMateriActivity, LinearLayoutManager.VERTICAL, false)
//            adapter = subMateriAdapter
//        }
//    }

//    private fun setupRvSubMateri(materiId: String) {
//        lifecycleScope.launchWhenStarted {
//            viewModel.subMateriList.collectLatest { result ->
//                when (result) {
//                    is Resource.Loading -> showLoading()
//                    is Resource.Success -> {
//                        hideLoading()
//                        subMateriAdapter.differ.submitList(result.data)
//                        // Update the clickability of items after submitting the list
//                        updateProgress(viewModel.progress.value)
//                    }
//                    is Resource.Error -> {
//                        hideLoading()
//                        Log.e("Sub Materi List", result.message.toString())
//                        Toast.makeText(this@SubMateriActivity, result.message, Toast.LENGTH_SHORT).show()
//                    }
//                    else -> Unit
//                }
//            }
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MateriActivity::class.java).apply {
            putExtra("navigateToHome", true)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProgress()
    }
}