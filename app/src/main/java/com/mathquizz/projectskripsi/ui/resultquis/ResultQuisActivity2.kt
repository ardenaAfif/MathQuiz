package com.mathquizz.projectskripsi.ui.resultquis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathquizz.projectskripsi.databinding.ActivityResultQuisBinding
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity4
import com.google.firebase.auth.FirebaseAuth
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ResultQuisActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityResultQuisBinding
    private val viewModel by viewModels<ResultQuisViewModel>()
    private var materiId: String? = null
    private var currentProgress: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "ResultQuisActivity2 dipanggil")   //Cek Pemanggilan

        enableEdgeToEdge()
        binding = ActivityResultQuisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)


        materiId = intent.getStringExtra("materiId")
        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 0)
        val answerResults = intent.getSerializableExtra("answerResults") as? ArrayList<Pair<Int, Boolean>> ?: arrayListOf()
        val scaledProgress = intent.getIntExtra("progress", 0)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { viewModel.setUserId(it) }
        materiId?.let { viewModel.setMateriId(it) }

        displayQuizResults(correctAnswers, totalQuestions, answerResults,scaledProgress )



        lifecycleScope.launchWhenStarted {
            viewModel.progress.collectLatest { progress ->
                currentProgress = progress

            }
        }

        binding.btnResult.setOnClickListener {
            viewModel.getMateriTitle { retrievedTitle ->
                val intent = Intent(this, SubMateriActivity4::class.java).apply {
                    materiId?.let { id ->
                        putExtra("materiId", id)
                    }
                    putExtra("title", retrievedTitle)

                }
                startActivity(intent)
                finish()
            }

        }
        materiId?.let { id ->
            viewModel.getMateriTitle { retrievedTitle ->
                title = retrievedTitle

            }
        }



    }



    private fun displayQuizResults(correctAnswers: Int, totalQuestions: Int, answerResults: ArrayList<Pair<Int, Boolean>>, scaledProgress: Int) {
        val scorePercentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
        val maxScore = 100
        val scaledProgress = (scorePercentage / 100.0 * maxScore).toInt()

        binding.piScore.max = maxScore
        binding.piScore.progress = scaledProgress
        binding.piScoreText.text = "$scaledProgress"


        val incorrectResults = answerResults.filter { !it.second }
        val resultsText = if (incorrectResults.isEmpty()) {
            "tidak ada Soal yang perlu diperbaiki"
        } else {
            incorrectResults.joinToString("\n") { (questionNumber, _) ->
                "Soal No $questionNumber"
            }
        }

        binding.tvResultSoal.text = resultsText

    }

    private fun navigateBasedOnProgress() {


    }

}