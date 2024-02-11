package com.luka.anidroid.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luka.anidroid.R;
import com.luka.anidroid.adapter.MusicVideoAdapter;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;
import com.luka.anidroid.model.MusicVideo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnimeDetailsActivity extends AppCompatActivity {

    private FavoritesManager favoritesManager;
    private Anime anime;
    private Button favoriteButton;
    List<MusicVideo> musicVideos;
    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();
    MusicVideoAdapter musicVideoAdapter;
    private TextToSpeech textToSpeech;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_CALENDAR_PERMISSION_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);

        favoritesManager = new FavoritesManager(this);
        anime = (Anime) getIntent().getSerializableExtra("anime");

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

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
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    saveImageToExternalStorage(bitmap);
                return true;
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
        descriptionTextView.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            } else {
                String description = descriptionTextView.getText().toString();
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

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
        broadcastDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we have the permission
                if (ContextCompat.checkSelfPermission(AnimeDetailsActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // If not, request the permission
                    ActivityCompat.requestPermissions(AnimeDetailsActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDAR_PERMISSION_REQUEST_CODE);
                } else {
                    // If we have the permission, proceed with adding the event to the calendar
                    addEventToCalendar(anime.getTitle(), anime.getBroadcastDay());
                }
            }
        });
        TextView episodesTextView = findViewById(R.id.anime_episodes);
        favoriteButton = findViewById(R.id.btn_favorite);
        TextView durationTextView = findViewById(R.id.anime_duration);
        TextView popularityTextView = findViewById(R.id.anime_popularity);
        TextView titleNativeTextView = findViewById(R.id.anime_title_native);
        titleNativeTextView.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            } else {
                String titleNative = titleNativeTextView.getText().toString();
                textToSpeech.setLanguage(Locale.JAPANESE);
                textToSpeech.speak(titleNative, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        TextView seasonTextView = findViewById(R.id.anime_season);
        TextView statusTextView = findViewById(R.id.anime_status);
        TextView typeTextView = findViewById(R.id.anime_type);

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
        descriptionTextView.setText("Description: " + anime.getDescription());
        scoreTextView.setText("Score: " + String.valueOf(anime.getAverageScore()));
        broadcastDayTextView.setText("Broadcast day: " + anime.getBroadcastDay());
        episodesTextView.setText("Number of episodes: " + String.valueOf(anime.getEpisodes()));
        durationTextView.setText("Duration: " + anime.getDuration());
        popularityTextView.setText("Popularity: " + String.valueOf(anime.getPopularity()));
        titleNativeTextView.setText("Title (native): " + anime.getTitleNative());
        seasonTextView.setText("Season: " + anime.getSeason());
        statusTextView.setText("Status: " + anime.getStatus());
        typeTextView.setText("Type: " + anime.getType());

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

        RecyclerView musicVideoRecyclerView = findViewById(R.id.music_video_recycler_view);
        musicVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        musicVideos = new ArrayList<>();
        fetchMusicVideos(anime.getId());
        musicVideoAdapter = new MusicVideoAdapter(musicVideos, this);
        musicVideoRecyclerView.setAdapter(musicVideoAdapter);
    }

    private void fetchMusicVideos(int id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://api.jikan.moe/v4/anime/" + id + "/videos")
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                // if the response is not successful, throw an exception
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Log.d("HomeFragment", "Response: " + responseBody);

                JsonNode root = objectMapper.readTree(responseBody);
                List<MusicVideo> newAnimeList = new ArrayList<>();

                JsonNode data = root.get("data");
                Log.d("HomeFragment", "Data: " + data.toString());
                Log.d("HomeFragment", "Music videos: " + data.get("music_videos").toString());
                JsonNode musicVideosNode = data.get("music_videos");

                for (JsonNode animeVideoNode : musicVideosNode) {
                    MusicVideo animeMusicVideo = new MusicVideo();
                    animeMusicVideo.setTitle(animeVideoNode.get("title").asText());
                    animeMusicVideo.setImageUrl(animeVideoNode.get("video").get("images").get("maximum_image_url").asText());
                    animeMusicVideo.setVideoUrl(animeVideoNode.get("video").get("embed_url").asText());
                    newAnimeList.add(animeMusicVideo);
                }

                handler.post(() -> {
                    musicVideos.clear();
                    musicVideos.addAll(newAnimeList);
                    Log.d("HomeFragment", "Number of anime in the newlist: " + newAnimeList.size());
                    Log.d("HomeFragment", "Number of anime in the list: " + musicVideos.size());
                    musicVideoAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Failed to fetch music videos", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Failed to fetch music videos", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void updateFavoriteButton() {
        if (favoritesManager.isFavorite(anime)) {
            favoriteButton.setText("Remove from Favorites");
        } else {
            favoriteButton.setText("Add to Favorites");
        }
    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image-" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();

        Uri uri = resolver.insert(externalContentUri, values);
        if (uri != null) {
            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (out != null) {
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with adding the event to the calendar
                addEventToCalendar(anime.getTitle(), anime.getBroadcastDay());
            } else {
                // Permission was denied. You can display a message to the user explaining why the permission is needed.
                Toast.makeText(this, "Permission needed to add events to calendar", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void addEventToCalendar(String title, String broadcastDay) {
        long startMillis = 0;
        long endMillis = 0;

        // Get the current date
        Calendar currentDate = Calendar.getInstance();
        int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        // Get the desired day of the week
        int desiredDayOfWeek = getBroadcastDay(broadcastDay);

        // Calculate the number of days until the next desired day of the week
        int daysToAdd = (desiredDayOfWeek - currentDayOfWeek + 7) % 7;

        // Set the start date to the next desired day of the week
        Calendar beginTime = Calendar.getInstance();
        beginTime.add(Calendar.DAY_OF_YEAR, daysToAdd);
        beginTime.set(Calendar.DAY_OF_WEEK, desiredDayOfWeek);
        startMillis = beginTime.getTimeInMillis();

        // Whole day event, so no specific end time
        endMillis = startMillis + TimeUnit.DAYS.toMillis(1);

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.ALL_DAY, 1); // Whole day event
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "Broadcast day for " + title);
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // Default calendar
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // Retrieve ID for new event
        long eventID = Long.parseLong(uri.getLastPathSegment());

        Toast.makeText(this, "Event added to calendar", Toast.LENGTH_SHORT).show();
    }

    private int getBroadcastDay(String broadcastDay) {
        switch (broadcastDay) {
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
            case "Sunday":
                return Calendar.SUNDAY;
            default:
                return -1; // Invalid broadcast day
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}