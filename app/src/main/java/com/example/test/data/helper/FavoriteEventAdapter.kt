package com.example.test.data.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.R
import com.example.test.databinding.ItemFavoriteBinding

class FavoriteEventAdapter(
    private val onFavoriteClicked: (FavoriteEvent) -> Unit,
    private val onItemClicked: (FavoriteEvent) -> Unit
) : ListAdapter<FavoriteEvent, FavoriteEventAdapter.FavoriteEventViewHolder>(
    FavoriteEventDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        val favoriteEvent = getItem(position)
        holder.bind(favoriteEvent, onFavoriteClicked, onItemClicked)
    }

    class FavoriteEventViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            favoriteEvent: FavoriteEvent,
            onFavoriteClicked: (FavoriteEvent) -> Unit,
            onItemClicked: (FavoriteEvent) -> Unit
        ) {
            binding.tvFavoriteName.text = favoriteEvent.name
            Glide.with(binding.root.context)
                .load(favoriteEvent.mediaCover)
                .into(binding.ivFavoriteImage)

            binding.ivFavoriteIcon.setImageResource(R.drawable.ic_favorite)
            binding.ivFavoriteIcon.setOnClickListener {
                onFavoriteClicked(favoriteEvent)
            }

            binding.root.setOnClickListener {
                onItemClicked(favoriteEvent)
            }
        }
    }

    class FavoriteEventDiffCallback : DiffUtil.ItemCallback<FavoriteEvent>() {
        override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem == newItem
        }
    }
}

