package com.mathquizz.projectskripsi.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.data.Materi
import com.mathquizz.projectskripsi.databinding.ItemMateriBinding
import com.mathquizz.projectskripsi.dialog.showCustomPopup
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity2
import com.mathquizz.projectskripsi.ui.submateri.SubMateriActivity3

class MateriAdapter(
    private val activity: Activity,
    private val context: Context,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MateriAdapter.MateriViewHolder>() {

    private var clickableItems: Map<String, Boolean> = emptyMap()

    private fun showPopup(message: String) {
        activity.showCustomPopup(message)
    }

    inner class MateriViewHolder(private val binding: ItemMateriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(materi: Materi, isLocked: Boolean) {
            binding.apply {
                tvTitle.text = materi.title

                scoreProgressIndicator.progress = materi.progress
                val progressText = "${materi.progress}%"
                scoreProgressText.text = progressText

                val isClickable = clickableItems[materi.materiId] ?: false
                itemView.isClickable = isClickable
                itemView.isEnabled = true
                if (isLocked) {
                    ivLockIcon.visibility = View.VISIBLE
                    scoreProgressText.visibility = View.GONE

                    tvCategory.text = "${materi.category} - Terkunci"
                    scoreProgressIndicator.trackColor = context.getColor(R.color.gray)
                    tvCategory.setTextColor(context.getColor(R.color.gray_dark))
                } else {
                    ivLockIcon.visibility = View.GONE
                    scoreProgressText.visibility = View.VISIBLE


                    tvCategory.text = materi.category
                    scoreProgressIndicator.trackColor = context.getColor(R.color.blue_4)
                    tvCategory.setTextColor(context.getColor(R.color.black))
                }
                itemView.setOnClickListener {
                    Log.d("MateriAdapter", "Item clicked: ${materi.materiId}, isClickable: $isClickable")
                    if (isClickable) {
                        val intent = if (adapterPosition <= 5) {
                            Intent(context, SubMateriActivity2::class.java).apply {
                                putExtra("materiId", materi.materiId)
                                putExtra("title", materi.title)
                            }
                        } else {
                            Intent(context, SubMateriActivity3::class.java).apply {
                                putExtra("materiId", materi.materiId)
                                putExtra("title", materi.title)
                            }
                        }
                        context.startActivity(intent)
                    } else {
                        showPopup("Materi Masih Terkunci!!")
                    }
                    onItemClick(materi.materiId)
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
        val isLocked = if (position == 0) {
            false
        } else {
            val previousMateri = differ.currentList.getOrNull(position - 1)
            previousMateri?.progress ?: 0 < 80
        }
        holder.bind(materi, isLocked)
        if (position == itemCount - 1) {
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                25,
                layoutParams.rightMargin,
                context.resources.getDimensionPixelSize(R.dimen.item_padding_bottom) // 50dp padding
            )
            holder.itemView.layoutParams = layoutParams
        } else {
            // Reset padding for other items if needed
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                25,
                layoutParams.rightMargin,
                5 // default padding
            )
            holder.itemView.layoutParams = layoutParams
        }
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