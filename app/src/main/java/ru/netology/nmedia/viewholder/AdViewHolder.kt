package ru.netology.nmedia.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Ad
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.load

class AdViewHolder(
    private val binding : CardAdBinding
) :RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad){
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}