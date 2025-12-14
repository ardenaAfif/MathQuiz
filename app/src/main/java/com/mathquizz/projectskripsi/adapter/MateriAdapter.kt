package com.mathquizz.projectskripsi.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.data.Materi
import com.mathquizz.projectskripsi.databinding.ItemMateriBinding
import com.mathquizz.projectskripsi.dialog.showCustomPopup

class MateriAdapter(
    private val context: Context,
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<MateriAdapter.MateriViewHolder>() {

    private var clickableItems: Map<String, Boolean> = emptyMap()

    inner class MateriViewHolder(private val binding: ItemMateriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(materi: Materi, isLocked: Boolean) {
            binding.apply {
                tvTitle.text = materi.title
                scoreProgressIndicator.progress = materi.progress
                scoreProgressText.text = "${materi.progress}%"

                val isClickable = clickableItems[materi.materiId] ?: false

                // Visual Update based on Lock Status
                if (isLocked) {
                    ivLockIcon.visibility = View.VISIBLE
                    scoreProgressText.visibility = View.GONE
                    tvCategory.text = "${materi.category} - Terkunci"
                    scoreProgressIndicator.trackColor =
                        ContextCompat.getColor(context, R.color.gray)
                    tvCategory.setTextColor(ContextCompat.getColor(context, R.color.gray_dark))
                } else {
                    ivLockIcon.visibility = View.GONE
                    scoreProgressText.visibility = View.VISIBLE
                    tvCategory.text = materi.category
                    scoreProgressIndicator.trackColor =
                        ContextCompat.getColor(context, R.color.blue_4)
                    tvCategory.setTextColor(ContextCompat.getColor(context, R.color.black))
                }

                itemView.setOnClickListener {
                    if (isClickable) {
                        onItemClick(materi.materiId, materi.title)
                    } else {
                        (context as? Activity)?.showCustomPopup("Materi Masih Terkunci!!")
                    }
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Materi>() {
        override fun areItemsTheSame(oldItem: Materi, newItem: Materi): Boolean {
            return oldItem.materiId == newItem.materiId
        }

        override fun areContentsTheSame(oldItem: Materi, newItem: Materi): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val binding = ItemMateriBinding.inflate(LayoutInflater.from(context), parent, false)
        return MateriViewHolder(binding)

        Log.d("ActivityCheck", "MateriAdapter dipanggil")   //Cek Pemanggilan

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val materi = differ.currentList[position]
        val isLocked = if (position == 0) false else {
            val prevItem = differ.currentList.getOrNull(position - 1)
            (prevItem?.progress ?: 0) < 80
        }

        holder.bind(materi, isLocked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateClickableItems(clickableItems: Map<String, Boolean>) {
        this.clickableItems = clickableItems
        notifyDataSetChanged() // Notify the adapter about data changes
    }
    @JvmSynthetic
    fun updateMateriList(newList: List<Materi>) {
        differ.submitList(newList)
    }
}