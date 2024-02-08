package com.luka.anidroid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.anidroid.R;
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
// Make network request to Anilist API, add data to animeList, and notify adapter
        // MOCK DATA
        Log.d("HomeFragment", "Loading page " + page);
        animeList.add(new Anime("Naruto", "https://cdn.myanimelist.net/images/anime/13/17405.jpg", "Naruto is a young shinobi with an incorrigible knack for mischief. He’s got a wild sense of humor, but Naruto is completely serious about his mission to be the world’s greatest ninja!", 2002, "TV"));
        animeList.add(new Anime("Naruto: Shippuden", "https://cdn.myanimelist.net/images/anime/5/17407.jpg", "Naruto: Shippuuden is the continuation of the original animated TV series Naruto. The story revolves around an older and slightly more matured Uzumaki Naruto and his quest to save his friend Uchiha Sasuke from the grips of the snake-like Shinobi, Orochimaru.", 2007, "TV"));
        animeList.add(new Anime("Evangelion: 3.0+1.0 Thrice Upon a Time", "https://cdn.myanimelist.net/images/anime/1000/110531.jpg", "The fourth and final film in the Rebuild of Evangelion series.", 2021, "Movie"));
        animeList.add(new Anime("Attack on Titan", "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "Several hundred years ago, humans were nearly exterminated by Titans. Titans are typically several stories tall, seem to have no intelligence, devour human beings and, worst of all, seem to do it for the pleasure rather than as a food source.", 2013, "TV"));
        animeList.add(new Anime("One Piece", "https://cdn.myanimelist.net/images/anime/6/73245.jpg", "Gol D. Roger was known as the Pirate King, the strongest and most infamous being to have sailed the Grand Line. The capture and execution of Roger by the World Government brought a change throughout the world.", 1999, "TV"));
        animeList.add(new Anime("Demon Slayer: Kimetsu no Yaiba", "https://cdn.myanimelist.net/images/anime/1286/99889.jpg", "Ever since the death of his father, the burden of supporting the family has fallen upon Tanjirou Kamado's shoulders. Though living impoverished on a remote mountain, the Kamado family are able to enjoy a relatively peaceful and happy life.", 2019, "TV"));

        Log.d("HomeFragment", "Data loaded, notifying adapter");
        animeAdapter.notifyItemInserted(animeList.size() - 1);
        isLoading = false;
    }
}