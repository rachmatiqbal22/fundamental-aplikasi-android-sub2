package com.example.test.data.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.example.test.databinding.ActivityDetailBinding
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.test.R
import com.example.test.data.database.AppDatabase
import com.example.test.data.database.FavoriteEventDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var favoriteEventDao: FavoriteEventDao
    private lateinit var favoriteEvent: LiveData<FavoriteEvent>
    private var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail"

        eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)
        val eventName = intent.getStringExtra(EXTRA_EVENT_NAME)
        val eventDate = intent.getStringExtra(EXTRA_EVENT_DATE)
        val eventDescription = intent.getStringExtra(EXTRA_EVENT_DESCRIPTION)
        val eventImage = intent.getStringExtra(EXTRA_EVENT_IMAGE)
        val eventLink = intent.getStringExtra(EXTRA_EVENT_LINK)
        val eventOrganizer = intent.getStringExtra(EXTRA_EVENT_ORGANIZER)
        val eventQuota = intent.getIntExtra(EXTRA_EVENT_QUOTA, 0)
        val eventRegistrants = intent.getIntExtra(EXTRA_EVENT_REGISTRANTS, 0)

        val remainingQuota = (eventQuota - eventRegistrants).coerceAtLeast(0)

        Log.d("DetailActivity", "Quota: $eventQuota, Registrants: $eventRegistrants")

        val eventFullDescription = intent.getStringExtra(EXTRA_EVENT_FULL_DESCRIPTION)

        binding.tvEventQuota.text = getString(R.string.quota_remaining, remainingQuota)
        binding.tvEventName.text = eventName
        binding.tvEventDate.text = eventDate
        binding.tvEventDescription.text = eventDescription
        binding.tvEventOrganizer.text = eventOrganizer
        binding.tvEventFullDescription.text = HtmlCompat.fromHtml(
            eventFullDescription.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        Glide.with(this)
            .load(eventImage)
            .into(binding.ivEventImage)

        binding.btnRegister.setOnClickListener {
            if (!eventLink.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventLink))
                startActivity(intent)
            }
        }

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "event-database").build()
        favoriteEventDao = db.favoriteEventDao()

        favoriteEvent = favoriteEventDao.getFavoriteEventById(eventId)

        favoriteEvent.observe(this) { favorite ->

            val drawable = binding.fabFavorite.drawable
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this,
                if(favorite == null){
                    R.color.purple_500
                }else{
                    R.color.white
                }))

            if (favorite != null) {
                binding.tvEventName.text = favorite.name
                binding.tvEventDate.text = eventDate
                binding.tvEventDescription.text = favorite.description
                binding.tvEventOrganizer.text = favorite.organizer
                val updatedRemainingQuota = (favorite.quota - favorite.registrants).coerceAtLeast(0)
                binding.tvEventQuota.text = getString(R.string.quota_remaining, updatedRemainingQuota)
                binding.tvEventFullDescription.text = HtmlCompat.fromHtml(
                    favorite.fullDescription.orEmpty(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                Glide.with(this)
                    .load(favorite.mediaCover)
                    .into(binding.ivEventImage)
            }
        }

        binding.fabFavorite.setOnClickListener {
            val favoriteEvent = FavoriteEvent(
                id = eventId,
                name = eventName.orEmpty(),
                description = eventDescription,
                organizer = eventOrganizer,
                quota = eventQuota,
                registrants = eventRegistrants,
                mediaCover = eventImage.orEmpty(),
                link = eventLink.orEmpty(),
                fullDescription = eventFullDescription.orEmpty()
            )


            CoroutineScope(Dispatchers.IO).launch {
                val existingFavorite = favoriteEventDao.isExistFavoriteEventById(eventId)
                if (existingFavorite) {
                    favoriteEventDao.delete(favoriteEvent)
                } else {
                    favoriteEventDao.insert(favoriteEvent)
                }

            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_NAME = "extra_event_name"
        const val EXTRA_EVENT_DATE = "extra_event_date"
        const val EXTRA_EVENT_DESCRIPTION = "extra_event_description"
        const val EXTRA_EVENT_IMAGE = "extra_event_image"
        const val EXTRA_EVENT_LINK = "extra_event_link"
        const val EXTRA_EVENT_ORGANIZER = "extra_event_organizer"
        const val EXTRA_EVENT_QUOTA = "extra_event_quota"
        const val EXTRA_EVENT_REGISTRANTS = "extra_event_registrants"
        const val EXTRA_EVENT_FULL_DESCRIPTION = "extra_event_full_description"
    }
}
