package com.luka.anidroid.fragment;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.luka.anidroid.R;
import com.luka.anidroid.activity.AnimeDetailsActivity;
import com.luka.anidroid.adapter.AnimeAdapter;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnimeAdapter animeAdapter;
    private FavoritesManager favoritesManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initializeFields(inflater, container);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(animeAdapter);

        InitializeSwipeRefreshLayout(view);

        return view;
    }

    @NonNull
    private View initializeFields(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesManager = new FavoritesManager(getContext());
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();

        recyclerView = view.findViewById(R.id.recyclerView);
        animeAdapter = new AnimeAdapter(favoriteAnimes);
        return view;
    }

    private void InitializeSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataAndCheckForNotifications();
            }
        });
    }

    private void refreshDataAndCheckForNotifications() {
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();
        animeAdapter.updateData(favoriteAnimes);

        for (Anime anime : favoriteAnimes) {
            Log.d("FavoritesFragment", "Checking anime: " + anime.getTitle() + "day: " + anime.getBroadcastDay());
            if (anime.isAiring() && anime.getBroadcastDay().equalsIgnoreCase(getCurrentDay())) {
                sendNotification(anime);
            }
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    private String getCurrentDay() {
        Log.d("FavoritesFragment", "Current day: " + LocalDate.now().getDayOfWeek().toString());
        return LocalDate.now().getDayOfWeek().toString();
    }

    private void sendNotification(Anime anime) {
        String channelId = "anime_channel_id";
        String channelName = "Anime Channel";
        int notificationId = anime.getId(); // unique id for each notification

        // Create a notification channel
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.areNotificationsEnabled()) {
            // Notifications are not enabled. Show a dialog to the user asking them to enable notifications.
            new AlertDialog.Builder(getContext())
                    .setTitle("Enable Notifications")
                    .setMessage("Please enable notifications to receive updates about your favorite animes.")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open the application settings screen
                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent that will be fired when the user taps the notification
        Intent intent = new Intent(getContext(), AnimeDetailsActivity.class);
        intent.putExtra("anime", (Serializable) anime);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // set the icon to use for this notification
                .setContentTitle(anime.getTitle()) // set the title of the notification
                .setContentText("New episode airing today!") // set the main text of the notification
                .setContentIntent(pendingIntent) // set the intent to fire when the user taps the notification
                .setAutoCancel(true); // automatically remove the notification when the user taps it

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list of favorite animes when the fragment is resumed
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();
        animeAdapter.updateData(favoriteAnimes);
    }
}
