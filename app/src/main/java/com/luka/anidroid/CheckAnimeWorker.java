package com.luka.anidroid;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.luka.anidroid.activity.AnimeDetailsActivity;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class CheckAnimeWorker extends Worker {

    public CheckAnimeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Fetch the updated list of favorite animes
        FavoritesManager favoritesManager = new FavoritesManager(getApplicationContext());
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();

        // Check if there are new episodes
        for (Anime anime : favoriteAnimes) {
            if (anime.isAiring() && anime.getBroadcastDay().equalsIgnoreCase(getCurrentDay())) {
                // If there are new episodes, send a notification
                sendNotification(anime);
            }
        }

        return Result.success();
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
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.areNotificationsEnabled()) {
            // Notifications are not enabled. Show a dialog to the user asking them to enable notifications.
            getNotificationPermissions();
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent that will be fired when the user taps the notification
        Intent intent = new Intent(getApplicationContext(), AnimeDetailsActivity.class);
        intent.putExtra("anime", (Serializable) anime);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // set the icon to use for this notification
                .setContentTitle(anime.getTitle()) // set the title of the notification
                .setContentText("New episode airing today!") // set the main text of the notification
                .setContentIntent(pendingIntent) // set the intent to fire when the user taps the notification
                .setAutoCancel(true); // automatically remove the notification when the user taps it

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }

    private void getNotificationPermissions() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Enable Notifications")
                .setMessage("Please enable notifications to receive updates about your favorite animes.")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the application settings screen
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName());
                        getApplicationContext().startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        return;
    }
}