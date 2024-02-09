package com.luka.anidroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luka.anidroid.R;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;

import org.w3c.dom.Text;

public class AnimeDetailsActivity extends AppCompatActivity {

    private FavoritesManager favoritesManager;
    private Anime anime;
    private Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);

        favoritesManager = new FavoritesManager(this);
        anime = (Anime) getIntent().getSerializableExtra("anime");

        ImageView imageView = findViewById(R.id.anime_image);
        TextView titleTextView = findViewById(R.id.anime_title);
        TextView descriptionTextView = findViewById(R.id.anime_description);
        TextView scoreTextView = findViewById(R.id.anime_score);
        TextView broadcastDayTextView = findViewById(R.id.anime_broadcast_day);
        TextView episodesTextView = findViewById(R.id.anime_episodes);
        favoriteButton = findViewById(R.id.btn_favorite);

        if (anime.getImageUrl() != null && !anime.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(anime.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground) // Show placeholder while image is loading
                    .error(R.drawable.ic_launcher_foreground) // Show placeholder if there's an error loading the image
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        }
        titleTextView.setText(anime.getTitle());
        descriptionTextView.setText(anime.getDescription());
        scoreTextView.setText(String.valueOf(anime.getAverageScore()));
        broadcastDayTextView.setText(anime.getBroadcastDay());
        episodesTextView.setText(String.valueOf(anime.getEpisodes()));

        favoriteButton.setOnClickListener(v -> {
            if (favoritesManager.isFavorite(anime)) {
                favoritesManager.removeFavorite(anime);
                Log.d("AnimeDetailsActivity", "Button clicked: Remove from Favorites");
            } else {
                favoritesManager.addFavorite(anime);
                Log.d("AnimeDetailsActivity", "Button clicked: Add to Favorites");
            }
            updateFavoriteButton();
        });

        updateFavoriteButton();
    }
    private void updateFavoriteButton() {
        if (favoritesManager.isFavorite(anime)) {
            favoriteButton.setText("Remove from Favorites");
        } else {
            favoriteButton.setText("Add to Favorites");
        }
    }
}