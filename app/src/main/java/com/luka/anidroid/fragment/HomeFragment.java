package com.luka.anidroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

// HomeFragment.java
public class HomeFragment extends Fragment {

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();
    private RecyclerView recyclerView;
    private AnimeAdapter animeAdapter;
    private List<Anime> animeList;
    private boolean isLoading = false;
    private int currentPage = 1;

    private static final String STATE_ANIME_LIST = "state_anime_list";
    private static final String STATE_CURRENT_PAGE = "state_current_page";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            animeList = new ArrayList<>();
            currentPage = 1;
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

                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == animeList.size() - 3) {
                    isLoading = true;
                    loadAnimeData(currentPage++);
                }
            }
        });

        loadAnimeData(currentPage++);

        return view;
    }

    private void loadAnimeData(int page) {
        Log.d("HomeFragment", "Loading page " + page);
        fetchAnimeData(animeList, page);
    }

    private void fetchAnimeData(List<Anime> animeList, int page) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        isLoading = true;

        executor.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://api.jikan.moe/v4/top/anime" + "?page=" + page)
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
                } catch (NullPointerException e) {
                    return;
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

                    anime.setDuration(animeNode.get("duration").asText());
                    anime.setPopularity(animeNode.get("popularity").asInt());
                    anime.setTitleNative(animeNode.get("title_japanese").asText());
                    anime.setTitleRomaji(animeNode.get("title_english").asText());
                    anime.setSeason(animeNode.get("season").asText());
                    anime.setStatus(animeNode.get("status").asText());
                    anime.setType(animeNode.get("type").asText());
                    newAnimeList.add(anime);
                }

                handler.post(() -> {
                    //animeList.clear();
                    animeList.addAll(newAnimeList);
                    Log.d("HomeFragment", "Number of anime in the newlist: " + newAnimeList.size());
                    Log.d("HomeFragment", "Number of anime in the list: " + animeList.size());
                    //animeAdapter.notifyDataSetChanged();
                    animeAdapter.notifyItemRangeInserted(animeList.size() - newAnimeList.size(), newAnimeList.size());
                    isLoading = false;
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(getContext(), "Failed to fetch anime data", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}