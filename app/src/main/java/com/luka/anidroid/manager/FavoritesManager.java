package com.luka.anidroid.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luka.anidroid.model.Anime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREFS_NAME = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorites_key";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public FavoritesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addFavorite(Anime anime) {
        List<Anime> favorites = getFavorites();
        favorites.add(anime);
        sharedPreferences.edit().putString(FAVORITES_KEY, gson.toJson(favorites)).apply();
        Log.d("FavoritesManager", "Anime added to favorites: " + anime.getTitle());
    }

    public void removeFavorite(Anime anime) {
        List<Anime> favorites = getFavorites();
        favorites.remove(anime);
        sharedPreferences.edit().putString(FAVORITES_KEY, gson.toJson(favorites)).apply();
        Log.d("FavoritesManager", "Anime removed from favorites: " + anime.getTitle());
    }

    public boolean isFavorite(Anime anime) {
        List<Anime> favorites = getFavorites();
        Log.d("FavoritesManager", "Checking if anime is favorite: " + anime.getTitle() + " " + favorites.contains(anime));
        Log.d("FavoritesManager", "Favorites: " + favorites.toString());
        return favorites.contains(anime);
    }

    public List<Anime> getFavorites() {
        String json = sharedPreferences.getString(FAVORITES_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Anime>>() {}.getType();
            return gson.fromJson(json, type);
        }
    }
}
