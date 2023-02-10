package ru.netology.nmedia.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.DateSeparator
import ru.netology.nmedia.TimeSeparator
import ru.netology.nmedia.databinding.CardSeparatorBinding

class DateSeparatorViewHolder (
    private val binding : CardSeparatorBinding
) :RecyclerView.ViewHolder(binding.root) {
    fun bind(date: DateSeparator){
        binding.date.text = when(date.time){
            TimeSeparator.TODAY -> "Today"
            TimeSeparator.YESTERDAY -> "Yesterday"
            TimeSeparator.LASTWEEK -> "Last week"
        }
    }
}