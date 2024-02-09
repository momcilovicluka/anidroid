package com.luka.anidroid.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luka.anidroid.R;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = anime.getTrailerUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        TextView titleTextView = findViewById(R.id.anime_title);
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = anime.getUrl();
                String title = anime.getTitle();
                String imageUrl = anime.getImageUrl();

                Glide.with(AnimeDetailsActivity.this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    File file = new File(getExternalCacheDir(), "shared_image.png");
                                    FileOutputStream out = new FileOutputStream(file);
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.close();

                                    Uri imageUri = FileProvider.getUriForFile(AnimeDetailsActivity.this, getPackageName() + ".provider", file);

                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                    intent.putExtra(Intent.EXTRA_TEXT, "Title: " + title + "\nURL: " + url);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(intent, "Share via"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        });

        TextView descriptionTextView = findViewById(R.id.anime_description);
        TextView scoreTextView = findViewById(R.id.anime_score);
        scoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = anime.getUrl();
                if (url != null && URLUtil.isValidUrl(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } else {
                    Log.e("AnimeDetailsActivity", "Invalid URL: " + url);
                    Toast.makeText(AnimeDetailsActivity.this, "Invalid URL: " + url, Toast.LENGTH_SHORT).show();
                }
            }
        });
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