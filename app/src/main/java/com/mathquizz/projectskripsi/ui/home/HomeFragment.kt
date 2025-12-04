package com.mathquizz.projectskripsi.ui.home

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathquizz.projectskripsi.adapter.MateriAdapter
import com.mathquizz.projectskripsi.databinding.FragmentHomeBinding
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var materiAdapter: MateriAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

        Log.d("ActivityCheck", "HomeFragment dipanggil")   //Cek Pemanggilan

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRvMateri()
        updateTimeOfDayGreeting()
    }

    override fun onResume() {
        super.onResume()
        getUser() // Refresh user info when returning to this fragment
        materiSetup() // Refresh materi list and check clickability
    }
    private fun getUser() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.user.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.tvWelcomeName.text = resource.data?.name ?: "User"
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateTimeOfDayGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greetingText = when (hour) {
            in 4..10 -> "Selamat Pagi"
            in 11..15 -> "Selamat Siang"
            in 16..18 -> "Selamat Sore"
            in 19..23 -> "Selamat Malam"
            else -> "Halo"
        }


        binding.tvWelcomeGreeting.text = greetingText
    }
    private fun setupRvMateri() {
        materiAdapter = MateriAdapter(requireActivity(),requireContext()) { materiId ->
            // Handle item click
            Log.d("HomeFragment", "Item clicked: $materiId")
        }
        binding.rvMateri.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = materiAdapter
        }
    }

    private fun materiSetup() {
        lifecycleScope.launchWhenStarted {
            viewModel.materiList.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        materiAdapter.differ.submitList(it.data)
                        viewModel.checkClickability()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Materi List", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.clickableItems.collect { clickableItems ->
                Log.d("Fragment", "Received clickable items: $clickableItems")
                materiAdapter.updateClickableItems(clickableItems) // Update clickable items
            }
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
