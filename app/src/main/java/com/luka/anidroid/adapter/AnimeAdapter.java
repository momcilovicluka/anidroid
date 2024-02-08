package com.luka.anidroid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luka.anidroid.R;
import com.luka.anidroid.model.Anime;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Anime> animeList;

    public AnimeAdapter(List<Anime> animeList) {
        this.animeList = animeList;
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.animeTitle.setText(anime.getTitle());
        holder.animeImage.setImageResource(R.drawable.ic_launcher_foreground);
        //holder.animeDescription.setText(anime.getDescription());
        Log.d("AnimeAdapter", "image url: " + anime.getImageUrl());
        if (anime.getImageUrl() != null && !anime.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(anime.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground) // Show placeholder while image is loading
                    .error(R.drawable.ic_launcher_foreground) // Show placeholder if there's an error loading the image
                    .into(holder.animeImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_launcher_foreground)
                    .into(holder.animeImage);
        }
        holder.animeScore.setText(String.valueOf(anime.getAverageScore()));
    }

    @Override
    public void onViewRecycled(@NonNull AnimeViewHolder holder) {
        super.onViewRecycled(holder);
        // Clear the image from the recycled view
        Glide.with(holder.itemView.getContext()).clear(holder.animeImage);
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {
        ImageView animeImage;
        TextView animeTitle;
        TextView animeDescription;
        TextView animeYear;
        TextView animeScore;

        public AnimeViewHolder(View itemView) {
            super(itemView);
            animeTitle = itemView.findViewById(R.id.anime_title);
            animeDescription = itemView.findViewById(R.id.anime_description);
            animeImage = itemView.findViewById(R.id.anime_image);
            animeScore = itemView.findViewById(R.id.anime_score);
        }
    }
}