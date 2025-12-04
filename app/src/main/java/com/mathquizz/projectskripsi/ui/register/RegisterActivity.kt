package com.mathquizz.projectskripsi.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.databinding.ActivityRegisterBinding
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.data.User
import com.mathquizz.projectskripsi.ui.login.LoginActivity
import com.mathquizz.projectskripsi.util.PreferenceHelper
import com.mathquizz.projectskripsi.util.RegisterValidation
import com.mathquizz.projectskripsi.util.Resource
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TAG ="RegisterActivity"
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: ActivityRegisterBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.apply {
            btnRegister.setOnClickListener{

                val user = User(

                    editTextNim.text.toString().trim().toLongOrNull() ?: 0,
                    editTextName.text.toString().trim(),
                    editTextProdi.text.toString().trim(),
                    editTextSemester.text.toString().trim().toIntOrNull() ?: 0,
                    editTextEmail.text.toString().trim()
                )


                val password = editTextPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when (it){
                    is Resource.Loading -> {
                        binding.pbRegister.visibility = View.VISIBLE
                        binding.btnRegister.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.pbRegister.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Log.d("test", it.data.toString())
                        PreferenceHelper.setLoggedIn(this@RegisterActivity, true)
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        binding.pbRegister.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Log.e(TAG, it.message.toString())
                    }
                    else -> Unit

                }
            }
        }
        lifecycleScope.launchWhenStarted {

            viewModel.validation.collect{validation ->
                if (validation.nim is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextNim.apply {
                            error = validation.nim.message
                        }
                    }
                }
                if (validation.name is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextName.apply {
                            error = validation.name.message
                        }
                    }
                }
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextEmail.apply {
                            error = validation.email.message
                        }
                    }
                }
                if (validation.prodi is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextProdi.apply {
                            error = validation.prodi.message
                        }
                    }
                }
                if (validation.semester is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextSemester.apply {
                            error = validation.semester.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextPassword.apply {
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }


}