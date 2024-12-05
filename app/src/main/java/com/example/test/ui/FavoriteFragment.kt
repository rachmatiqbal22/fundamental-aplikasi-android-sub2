package com.example.test.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.data.helper.FavoriteEvent
import com.example.test.R
import com.example.test.data.database.AppDatabase
import com.example.test.data.database.FavoriteEventDao
import com.example.test.data.helper.DetailActivity
import com.example.test.data.helper.FavoriteEventAdapter
import com.example.test.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteEventDao: FavoriteEventDao
    private lateinit var adapter: FavoriteEventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoriteBinding.bind(view)

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "event-database"
        ).build()
        favoriteEventDao = db.favoriteEventDao()

        // Setup RecyclerView
        adapter = FavoriteEventAdapter(
            onFavoriteClicked = { favoriteEvent ->
                toggleFavoriteStatus(favoriteEvent)
            },
            onItemClicked = { favoriteEvent ->
                openDetailActivity(favoriteEvent)
            }
        )
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFavorites.adapter = adapter

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        // Observe favorites from database
        favoriteEventDao.getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
                binding.recyclerViewFavorites.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.recyclerViewFavorites.visibility = View.VISIBLE
                adapter.submitList(favorites)
            }
        }
    }

    private fun toggleFavoriteStatus(favoriteEvent: FavoriteEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingFavorite = favoriteEventDao.getFavoriteEventById(favoriteEvent.id).value
            if (existingFavorite != null) {
                favoriteEventDao.delete(favoriteEvent)
            } else {
                favoriteEventDao.insert(favoriteEvent)
            }
        }
    }

    private fun openDetailActivity(favoriteEvent: FavoriteEvent) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, favoriteEvent.id)
            putExtra(DetailActivity.EXTRA_EVENT_NAME, favoriteEvent.name)
            putExtra(DetailActivity.EXTRA_EVENT_IMAGE, favoriteEvent.mediaCover)
            putExtra(DetailActivity.EXTRA_EVENT_LINK, favoriteEvent.link)
        }
        startActivity(intent)
    }
}
