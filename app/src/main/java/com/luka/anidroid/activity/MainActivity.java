package com.luka.anidroid.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

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

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final float LIGHT_THRESHOLD = 80.0f;
    private static final long THEME_CHANGE_COOLDOWN = 5000; // Cooldown in milliseconds
    private long lastThemeChangeTime = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of your activity or any data you want to retain
        // For example, you can save the selected item id of the BottomNavigationView
        outState.putInt("selectedItemId", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the saved state of your activity or any data
        // For example, you can restore the selected item id of the BottomNavigationView
        int selectedItemId = savedInstanceState.getInt("selectedItemId");
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set HomeFragment as default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set home item as selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = homeFragment;
            } else if (item.getItemId() == R.id.navigation_favorites) {
                selectedFragment = favoritesFragment;
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}