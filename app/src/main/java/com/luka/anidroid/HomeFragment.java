package com.luka.anidroid;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.anidroid.adapter.AnimeAdapter;
import com.luka.anidroid.model.Anime;

import java.util.ArrayList;
import java.util.List;

// HomeFragment.java
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AnimeAdapter animeAdapter;
    private List<Anime> animeList;
    private boolean isLoading = false;
    private int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        animeList = new ArrayList<>();
        animeAdapter = new AnimeAdapter(animeList);

        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(animeAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == animeList.size() - 1) {
                    // End of the list is reached, load more data
                    loadAnimeData(currentPage + 1);
                    isLoading = true;
                }
            }
        });

        return view;
    }

    private void loadAnimeData(int page) {
        // Make network request to Anilist API, add data to animeList, and notify adapter
        // MOCK DATA
        Log.d("HomeFragment", "loadAnimeData called with page: " + page);
        List<Anime> newAnimeList = new ArrayList<>();

        Log.d("HomeFragment", "Number of anime in the list: " + animeList.size());

        animeAdapter.notifyDataSetChanged();
        isLoading = false;
    }
}