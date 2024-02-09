package com.luka.anidroid.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.luka.anidroid.R;
import com.luka.anidroid.fragment.FavoritesFragment;
import com.luka.anidroid.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

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
            } else if (item.getItemId() == R.id.navigation_settings) {
                //selectedFragment = new SettingsFragment();
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });
    }
}