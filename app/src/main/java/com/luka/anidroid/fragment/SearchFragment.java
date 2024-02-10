package com.luka.anidroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luka.anidroid.R;
import com.luka.anidroid.adapter.AnimeAdapter;
import com.luka.anidroid.model.Anime;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private AnimeAdapter animeAdapter;

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();
    private List<Anime> animeList;
    private int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        animeList = new ArrayList<>();
        animeAdapter = new AnimeAdapter(animeList);
        recyclerView.setAdapter(animeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint("Search for anime");
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Make the API call and update the RecyclerView
                searchAnime(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optionally, you can make the API call as the user types
                return false;
            }
        });
        return view;
    }

    private void searchAnime(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://api.jikan.moe/v4/anime" + "?q=" + query)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                // if the response is not successful, throw an exception
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Log.d("HomeFragment", "Response: " + responseBody);

                JsonNode root = objectMapper.readTree(responseBody);
                List<Anime> newAnimeList = new ArrayList<>();

                JsonNode data = root.get("data");
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(data.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JsonNode animeNode = data.get(i);
                    Anime anime = new Anime();
                    anime.setId(animeNode.get("mal_id").asInt());
                    anime.setTitle(animeNode.get("title").asText());
                    anime.setDescription(animeNode.get("synopsis").asText());
                    anime.setImageUrl(animeNode.get("images").get("jpg").get("large_image_url").asText());
                    anime.setAverageScore(animeNode.get("score").asDouble());
                    anime.setAiring(animeNode.get("airing").asBoolean());
                    anime.setEpisodes(animeNode.get("episodes").asInt());
                    anime.setTrailerUrl(animeNode.get("trailer").get("embed_url").asText());
                    anime.setBroadcastDay(animeNode.get("broadcast").get("day").asText());
                    anime.setBroadcastDay(anime.getBroadcastDay().substring(0, anime.getBroadcastDay().length() - 1));
                    anime.setUrl(animeNode.get("url").asText());
                    newAnimeList.add(anime);
                }

                handler.post(() -> {
                    animeList.clear();
                    animeList.addAll(newAnimeList);
                    Log.d("HomeFragment", "Number of anime in the newlist: " + newAnimeList.size());
                    Log.d("HomeFragment", "Number of anime in the list: " + animeList.size());
                    //animeAdapter.notifyDataSetChanged();
                    animeAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}