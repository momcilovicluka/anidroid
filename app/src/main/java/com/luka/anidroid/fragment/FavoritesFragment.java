package com.luka.anidroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.anidroid.R;
import com.luka.anidroid.adapter.AnimeAdapter;
import com.luka.anidroid.manager.FavoritesManager;
import com.luka.anidroid.model.Anime;

import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnimeAdapter animeAdapter;
    private FavoritesManager favoritesManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesManager = new FavoritesManager(getContext());
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();

        recyclerView = view.findViewById(R.id.recyclerView);
        animeAdapter = new AnimeAdapter(favoriteAnimes);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(animeAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list of favorite animes when the fragment is resumed
        List<Anime> favoriteAnimes = favoritesManager.getFavorites();
        animeAdapter.updateData(favoriteAnimes);
    }
}
