package com.mathquizz.projectskripsi.ui.profile


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.databinding.FragmentProfileBinding
import com.mathquizz.projectskripsi.dialog.setupChangePasswordDialog
import com.mathquizz.projectskripsi.dialog.setupWarningMessageDialog
import com.mathquizz.projectskripsi.ui.about.AboutActivity
import com.mathquizz.projectskripsi.ui.about.AboutUsageActivity
import com.mathquizz.projectskripsi.ui.about.DevelopActivity
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var materiId: String? = null
    private var currentImageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

        Log.d("ActivityCheck", "ProfileFragment dipanggil")   //Cek Pemanggilan
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            materiId = it.getString("materiId")
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.user.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.apply {
                            tvExampleNim.text = resource.data?.nim?.toString() ?: "User"
                            tvProfilName.text = resource.data?.name ?: "User"
                            tvExampleEmail.text = resource.data?.email ?: "User"
                            tvExampleProdi.text = resource.data?.prodi ?: "User"
                            tvExampleSemester.text =
                                "||  Semester : ${resource.data?.semester?.toString() ?: "User"}"
                            val imagePath = resource.data?.imagePath
                            if (imagePath.isNullOrEmpty()) {
                                // Jika imagePath kosong, set gambar default dengan border
                                Glide.with(requireContext())
                                    .load(R.drawable.img_3d_2)
                                    .into(circleImageView)
                            } else {
                                // Jika imagePath tidak kosong, load gambar dengan border menggunakan Glide
                                Glide.with(requireContext())
                                    .load(imagePath)
                                    .into(circleImageView)
                            }

                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
        binding.exit.setOnClickListener { // Create a confirmation dialog
            showResetProgressWarningDialog()

        }
        binding.aboutAplication.setOnClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
            activity
        }
        binding.aboutDevelopers.setOnClickListener {
            val intent = Intent(activity, DevelopActivity::class.java)
            startActivity(intent)
            activity
        }
        binding.aboutToUse.setOnClickListener {
            val intent = Intent(activity, AboutUsageActivity::class.java)
            startActivity(intent)
            activity
        }
        binding.passwordSetting.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.circleImageView.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uploadState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun showResetProgressWarningDialog() {
        setupWarningMessageDialog("untuk keluar dari Akun Anda?") {


        }


    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri.let {
            currentImageUri = it
            if (it != null) {
                viewModel.uploadProfileImage(it)
            }
        }
    }
    private fun showChangePasswordDialog() {
        requireActivity().setupChangePasswordDialog { oldPassword, newPassword, confirmNewPassword ->
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT)
                    .show()
                return@setupChangePasswordDialog
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(
                    requireContext(),
                    "Kata sandi baru dan yang dikonfirmasi tidak cocok",
                    Toast.LENGTH_SHORT
                ).show()
                return@setupChangePasswordDialog
            }

            // Change password
            viewModel.changePassword(oldPassword, newPassword)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.passwordChangeResult.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "Kata sandi berhasil diubah",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                resource.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // Handle other states if necessary
                        else -> Unit
                    }
                }
            }
        }
    }
}