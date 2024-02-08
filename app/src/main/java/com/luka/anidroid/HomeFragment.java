package com.luka.anidroid;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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

        loadAnimeData(currentPage);

        return view;
    }

    private void loadAnimeData(int page) {
        // Make network request to Anilist API, add data to animeList, and notify adapter
        // MOCK DATA
        Log.d("HomeFragment", "loadAnimeData called with page: " + page);
        animeList.add(new Anime("Naruto", "https://cdn.myanimelist.net/images/anime/13/17405.jpg", "Naruto is a young ninja who seeks recognition from his peers and dreams of becoming the Hokage, the leader of his village.", 2002, "TV"));
        animeList.add(new Anime("One Piece", "https://cdn.myanimelist.net/images/anime/6/73245.jpg", "Gol D. Roger was known as the Pirate King, the strongest and most infamous being to have sailed the Grand Line. The capture and execution of Roger by the World Government brought a change throughout the world.", 1999, "TV"));
        animeList.add(new Anime("Bleach", "https://cdn.myanimelist.net/images/anime/3/40451.jpg", "15-year-old Kurosaki Ichigo is not your everyday high school student. He has from as far he can remember always had the ability to see ghosts and spirits.", 2004, "TV"));
        animeList.add(new Anime("Dragon Ball Z", "https://cdn.myanimelist.net/images/anime/6/50135.jpg", "Five years after winning the World Martial Arts tournament, Gokuu is now living a peaceful life with his wife and son.", 1989, "TV"));
        animeList.add(new Anime("Attack on Titan", "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "Centuries ago, mankind was slaughtered to near extinction by monstrous humanoid creatures called titans, forcing humans to hide in fear behind enormous concentric walls.", 2013, "TV"));
// After adding anime data
        Log.d("HomeFragment", "Number of anime in the list: " + animeList.size());

        animeAdapter.notifyDataSetChanged();
        isLoading = false;
    }
}