package com.mathquizz.projectskripsi.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathquizz.projectskripsi.data.SubMateri
import com.mathquizz.projectskripsi.databinding.ItemSubmateriBinding
import com.mathquizz.projectskripsi.dialog.showQuizDialog
import com.mathquizz.projectskripsi.ui.modul.ModulActivity2
import com.mathquizz.projectskripsi.ui.quiz.QuisActivity2

class SubMateriAdapter2(
    private val context: Context,
    private val materiId: String,
    private val startModulActivityLauncher: ActivityResultLauncher<Intent>,
    var isProgressSufficient: Boolean // Check if progress is sufficient
) : RecyclerView.Adapter<SubMateriAdapter2.SubMateriViewHolder>() {

    inner class SubMateriViewHolder(private val binding: ItemSubmateriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun showPopup(message: String) {
            // You need to implement or call a method to show popup
            (context as? Activity)?.showQuizDialog(message)
        }

        fun bind(subMateri: SubMateri, position: Int) {
            binding.apply {
                tvsubTitle.text = subMateri.title
                tvsubCategory.text = subMateri.category

                Glide.with(itemView)
                    .load(subMateri.imageURL)
                    .centerCrop()
                    .into(ivsubMateri)
                if (position == 2) {
                    binding.imgLock.visibility = if (isProgressSufficient) View.GONE else View.VISIBLE
                } else {
                    binding.imgLock.visibility = View.GONE
                }
                // Set the clickability of the item based on its position and progress
                val isClickable = position in listOf(0, 1) || isProgressSufficient
                itemView.isClickable = isClickable
                itemView.isEnabled = true

                itemView.setOnClickListener {
                    if (isClickable) {
                        val intent = when (position) {
                            2 -> Intent(context, QuisActivity2::class.java).apply {
                                putExtra("materiId", materiId)
                                putExtra("submateriId", subMateri.submateriId)
                                putExtra("title", subMateri.title)
                            }
                            1 -> Intent(context, ModulActivity2::class.java).apply {
                                putExtra("materiId", materiId)
                                putExtra("submateriId", subMateri.submateriId)
                                putExtra("title", subMateri.title)
                            }
                            else -> Intent(context, ModulActivity2::class.java).apply {
                                putExtra("materiId", materiId)
                                putExtra("submateriId", subMateri.submateriId)
                                putExtra("title", subMateri.title)
                            }
                        }
                        startModulActivityLauncher.launch(intent)
                    } else {
                        showPopup("Pelajari Materi dan Contoh Soal\nTerlebih Dahulu!!!")
                    }
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<SubMateri>() {
        override fun areItemsTheSame(oldItem: SubMateri, newItem: SubMateri): Boolean {
            return oldItem.submateriId == newItem.submateriId
        }

        override fun areContentsTheSame(oldItem: SubMateri, newItem: SubMateri): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubMateriViewHolder {
        return SubMateriViewHolder(
            ItemSubmateriBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
        Log.d("ActivityCheck", "SubMateriAdapter2 dipanggil")   //Cek Pemanggilan

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SubMateriViewHolder, position: Int) {
        val subMateri = differ.currentList[position]
        holder.bind(subMateri, position)
    }
}