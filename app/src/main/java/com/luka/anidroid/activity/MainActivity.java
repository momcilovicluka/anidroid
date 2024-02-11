package com.luka.anidroid.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.luka.anidroid.CheckAnimeWorker;
import com.luka.anidroid.R;
import com.luka.anidroid.fragment.FavoritesFragment;
import com.luka.anidroid.fragment.HomeFragment;
import com.luka.anidroid.fragment.SearchFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final float LIGHT_THRESHOLD = 80.0f;
    private static final long THEME_CHANGE_COOLDOWN = 5000; // Cooldown in milliseconds
    private long lastThemeChangeTime = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItemId", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = savedInstanceState.getInt("selectedItemId");
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set HomeFragment as default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favoritesFragment).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set home item as selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_favorites);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = homeFragment;
            } else if (item.getItemId() == R.id.navigation_favorites) {
                selectedFragment = favoritesFragment;
            } else if (item.getItemId() == R.id.action_search) {
                selectedFragment = searchFragment;
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });

        lastThemeChangeTime = System.currentTimeMillis();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        PeriodicWorkRequest checkAnimeWorkRequest = new PeriodicWorkRequest.Builder(CheckAnimeWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("CheckAnime", ExistingPeriodicWorkPolicy.KEEP, checkAnimeWorkRequest);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastThemeChangeTime > THEME_CHANGE_COOLDOWN) {
                if (lightValue > LIGHT_THRESHOLD) {
                    // Light theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    // Dark theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                lastThemeChangeTime = currentTime;
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.action_search);
            EditText searchEditText = findViewById(R.id.search_view);

            searchFragment.searchAnime(sharedText);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}