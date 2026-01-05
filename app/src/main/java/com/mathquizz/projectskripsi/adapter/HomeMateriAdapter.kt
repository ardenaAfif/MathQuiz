package com.mathquizz.projectskripsi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.databinding.ItemMateriHomeBinding

class HomeMateriAdapter (
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<HomeMateriAdapter.ViewHolder>() {

    data class HomeMateriModel(
        val title: String,
        val imageRes: Int,
        val collectionName: String
    )

    private val listData = listOf(
        HomeMateriModel("Materi Turunan", R.drawable.img_3d_3, "materi"),
        HomeMateriModel("Materi Integral", R.drawable.img_shofar_3d1, "materiintegral")
    )

    inner class ViewHolder(private val binding: ItemMateriHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeMateriModel) {
            binding.tvMateriName.text = item.title
            binding.ivMateri.setImageResource(item.imageRes)

            binding.btnToMateri.setOnClickListener {
                onClick(item.collectionName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMateriHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

}