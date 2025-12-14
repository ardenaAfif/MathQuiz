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
import com.mathquizz.projectskripsi.dialog.showCustomPopup
import com.mathquizz.projectskripsi.dialog.showQuizDialog
import com.mathquizz.projectskripsi.ui.modul.ModulActivity
import com.mathquizz.projectskripsi.ui.quiz.QuisActivity
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity

class SubMateriAdapter(
    private val context: Context,
    private val materiId: String,
    private val collectionName: String,
    private val adapterType: Int, // 1 = Quiz di posisi 2, 2 = Quiz di posisi 3
    private val startModulActivityLauncher: ActivityResultLauncher<Intent>,
    var isProgressSufficient: Boolean
) : RecyclerView.Adapter<SubMateriAdapter.SubMateriViewHolder>() {

    private val quizPosition: Int
        get() = if (adapterType == 1) 2 else 3

    inner class SubMateriViewHolder(private val binding: ItemSubmateriBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subMateri: SubMateri, position: Int) {
            binding.apply {
                tvsubTitle.text = subMateri.title
                tvsubCategory.text = subMateri.category

                Glide.with(itemView)
                    .load(subMateri.imageURL)
                    .centerCrop()
                    .into(ivsubMateri)

                val isLocked = (position == quizPosition && !isProgressSufficient)
                binding.imgLock.visibility = if (isLocked) View.VISIBLE else View.GONE

                // Set the clickability of the item based on its position and progress
                val isClickable = (position != quizPosition) || isProgressSufficient

                itemView.alpha = if(isClickable) 1.0f else 0.5f
                itemView.isEnabled = isClickable

                itemView.setOnClickListener {
                    if (isClickable) {
                        val intent: Intent = if (position == quizPosition) {
                            // --- MASUK KE QUIZ ---
                            Intent(context, QuisActivity::class.java).apply {
                                putExtra("materiId", materiId)
                                putExtra("submateriId", subMateri.submateriId)
                                putExtra("title", subMateri.title)
                                putExtra("collectionName", collectionName)
                            }
                        } else {
                            // --- MASUK KE MODUL ---
                            Intent(context, ModulActivity::class.java).apply {
                                putExtra("materiId", materiId)
                                putExtra("submateriId", subMateri.submateriId)
                                putExtra("title", subMateri.title)
                                putExtra("collectionName", collectionName)
                            }
                        }
                        startModulActivityLauncher.launch(intent)
                    } else {
                        (context as? Activity)?.showCustomPopup("Pelajari Materi dan Contoh Soal\nTerlebih Dahulu!")
                    }

                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<SubMateri>() {
        override fun areItemsTheSame(oldItem: SubMateri, newItem: SubMateri): Boolean = oldItem.submateriId == newItem.submateriId
        override fun areContentsTheSame(oldItem: SubMateri, newItem: SubMateri): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubMateriViewHolder {
        return SubMateriViewHolder(
            ItemSubmateriBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SubMateriViewHolder, position: Int) {
        holder.bind(differ.currentList[position], position)
    }
}