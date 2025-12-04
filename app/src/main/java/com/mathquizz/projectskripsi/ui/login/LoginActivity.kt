package com.mathquizz.projectskripsi.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathquizz.projectskripsi.databinding.ActivityLoginBinding
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.dialog.setupBottomSheetDialog
import com.mathquizz.projectskripsi.ui.register.RegisterActivity
import com.mathquizz.projectskripsi.ui.register.TAG
import com.mathquizz.projectskripsi.util.LoginValidation
import com.mathquizz.projectskripsi.util.PreferenceHelper
import com.mathquizz.projectskripsi.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "LoginActivity dipanggil")   //Cek Pemanggilan


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeLoginState()
        observeResetPasswordState()
        observeValidationState()

    }


    private fun setupUI() {

        binding.apply {
            btnLogin.setOnClickListener {
                val email = editTextEmailLogin.text.toString().trim()
                val password = editTextPasswordLogin.text.toString()
                viewModel.login(email, password)
            }

            tvSignUpLogin.setOnClickListener {
                navigateToRegister()
            }

            tvForgotPassword.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }

        }

    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.pbLogin.visibility = View.VISIBLE
                        binding.btnLogin.isEnabled = false
                    }

                    is Resource.Success -> {
                        binding.pbLogin.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        PreferenceHelper.setLoggedIn(this@LoginActivity, true)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }

                    is Resource.Error -> {
                        binding.pbLogin.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        Snackbar.make(binding.root, "${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                        Log.e(TAG, it.message.toString())
                    }

                    else -> Unit

                }
            }
        }
    }

    private fun observeResetPasswordState() {
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            binding.root,
                            "Tautan Reset Telah Dikirim ke Email Anda",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }

                    is Resource.Error -> {
                        Snackbar.make(binding.root, "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit

                }
            }
        }
    }

    private fun observeValidationState() {
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is LoginValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.editTextEmailLogin.apply {
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is LoginValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.editTextPasswordLogin.apply {
                            error = validation.password.message
                        }
                    }
                }
            }

        }
    }

}