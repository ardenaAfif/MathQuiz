package com.mathquizz.projectskripsi.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.data.Quis
import com.mathquizz.projectskripsi.databinding.ActivityQuisBinding
import com.mathquizz.projectskripsi.ui.resultquis.ResultQuisActivity2
import com.mathquizz.projectskripsi.util.Resource
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuisActivity2 : AppCompatActivity() {


    private lateinit var binding: ActivityQuisBinding
    private val viewModel by viewModels<QuisViewModel>()
    private var currentQuestionIndex = 0
    private var selectedButton: MaterialCardView? = null
    private lateinit var currentQuestions: List<Quis>
    private val answerResults = mutableListOf<Pair<Int, Boolean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "QuisActivity2 dipanggil")   //Cek Pemanggilan

        try {
            binding = ActivityQuisBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            Log.e("QuisActivity", "Error initializing binding", e)
            // Handle the error if binding fails
        }

        val materiId = intent.getStringExtra("materiId")
        val submateriId = intent.getStringExtra("submateriId")
        val title = intent.getStringExtra("title")

        if (materiId != null && submateriId !=null) {
            viewModel.setQuisId(materiId, submateriId)
        }
        binding.tvList.text = title
        setupObservers()
        setupButtonListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.quisQuestion.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading state if needed
                    }
                    is Resource.Success -> {
                        currentQuestions = resource.data ?: emptyList()
                        updateUI()
                    }
                    is Resource.Error -> {
                        // Handle error state if needed
                    }
                    else -> {
                        // Handle unspecified state if needed
                    }
                }
            }
        }
    }

    private fun updateUI() {
        if (currentQuestions.isNotEmpty()) {
            val question = currentQuestions[currentQuestionIndex]

            binding.tvSoal.text = question.question

            binding.tvAditionalSoal.text = question.aditionalquestion

            question.imgquestion?.let {  imageUrl ->
                loadImage(binding.ivSoal, imageUrl)
            }

            loadImage(binding.ivA, question.optionA)
            loadImage(binding.ivB, question.optionB)
            loadImage(binding.ivC, question.optionC)
            loadImage(binding.ivD, question.optionD)
            loadImage(binding.ivE, question.optionE)

            val progress = ((currentQuestionIndex + 1) / currentQuestions.size.toFloat() * 100).toInt()
            binding.piQuis.progress = progress

            binding.tvNumberQuis.text = getString(R.string.question_1_5, currentQuestionIndex + 1, currentQuestions.size)

            binding.tvAditionalSoal.visibility = if (question.aditionalquestion.isNullOrBlank()) View.GONE else View.VISIBLE
        }


    }

    private fun loadImage(imageView: ImageView, url: String) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.img_quiz)
            .diskCacheStrategy(DiskCacheStrategy.ALL)  // Gambar placeholder jika gagal memuat
            .into(imageView)
    }
    private fun setupButtonListeners() {
        val buttons = listOf(
            binding.cvA,
            binding.cvB,
            binding.cvC,
            binding.cvD,
            binding.cvE
        )

        for (button in buttons) {
            button.setOnClickListener {
                selectedButton?.setCardBackgroundColor(resources.getColor(R.color.white, null)) // Reset previous selected button color
                button.setCardBackgroundColor(resources.getColor(R.color.blue_1, null)) // Change color to blue
                selectedButton = button // Keep track of the selected button
            }
        }

        binding.btnQuis.setOnClickListener {
            if (selectedButton != null) {
                val selectedIndex = buttons.indexOf(selectedButton)
                val selectedImageUrl = when (selectedIndex) {
                    0 -> currentQuestions[currentQuestionIndex].optionA
                    1 -> currentQuestions[currentQuestionIndex].optionB
                    2 -> currentQuestions[currentQuestionIndex].optionC
                    3 -> currentQuestions[currentQuestionIndex].optionD
                    4 -> currentQuestions[currentQuestionIndex].optionE
                    else -> ""
                }

                val isCorrect = selectedImageUrl == currentQuestions[currentQuestionIndex].answer
                answerResults.add(Pair(currentQuestionIndex + 1, isCorrect)) // Store question number and correctness

                selectedButton?.setCardBackgroundColor(
                    if (isCorrect) resources.getColor(R.color.blue_1, null)
                    else resources.getColor(R.color.blue_1, null)
                )

                lifecycleScope.launch {
                    kotlinx.coroutines.delay(50) // Delay for user to see the result
                    selectedButton?.setCardBackgroundColor(resources.getColor(R.color.white, null)) // Reset color
                    selectedButton = null

                    currentQuestionIndex++
                    if (currentQuestionIndex < currentQuestions.size) {
                        updateUI()
                    } else {
                        onQuizEnd()
                    }
                }
            } else {
                // Handle case where no option is selected
            }
        }
    }
    private fun onQuizEnd() {
        val correctAnswers = answerResults.count { it.second }
        val totalQuestions = currentQuestions.size
        val scorePercentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()

        val maxScore = 50
        val scaledProgress = (scorePercentage / 100.0 * maxScore).toInt()
        val materiId = intent.getStringExtra("materiId") ?: return
        val submateriId = intent.getStringExtra("submateriId") ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.updateProgressBasedOnQuiz(userId, correctAnswers)
        lifecycleScope.launch {
            try {

                val intent = Intent(this@QuisActivity2, ResultQuisActivity2::class.java).apply {
                    putExtra("correctAnswers", correctAnswers)
                    putExtra("totalQuestions", totalQuestions)
                    putExtra("answerResults", ArrayList(answerResults)) // Convert to ArrayList
                    putExtra("progress", scaledProgress)
                    putExtra("materiId", materiId)
                    putExtra("submateriId", submateriId)
                }
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("QuisActivity", "Error updating progress", e)
            }
        }
    }
}