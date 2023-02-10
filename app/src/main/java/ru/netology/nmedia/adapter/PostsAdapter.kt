package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.*
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardSeparatorBinding
import ru.netology.nmedia.databinding.PostLayoutBinding
import ru.netology.nmedia.viewholder.*

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) :
    PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallBack()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.post_layout
            is DateSeparator -> R.layout.card_separator
            else -> error("unknown view type ")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.post_layout -> {
                val binding =
                    PostLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AdViewHolder(binding)
            }
            R.layout.card_separator ->{
                val binding =
                    CardSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DateSeparatorViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is DateSeparator -> (holder as? DateSeparatorViewHolder)?.bind(item)
            else -> error("unkown view type ")
        }
    }
}