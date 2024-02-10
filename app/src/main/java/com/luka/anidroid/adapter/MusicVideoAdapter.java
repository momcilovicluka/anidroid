package com.luka.anidroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luka.anidroid.R;
import com.luka.anidroid.model.MusicVideo;

import java.util.List;

public class MusicVideoAdapter extends RecyclerView.Adapter<MusicVideoAdapter.MusicVideoViewHolder> {
    private List<MusicVideo> musicVideos;
    private Context context;

    public MusicVideoAdapter(List<MusicVideo> musicVideos, Context context) {
        this.musicVideos = musicVideos;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_video, parent, false);
        return new MusicVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicVideoViewHolder holder, int position) {
        MusicVideo musicVideo = musicVideos.get(position);
        holder.bind(musicVideo);
    }

    @Override
    public int getItemCount() {
        return musicVideos.size();
    }

    class MusicVideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        MusicVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }

        void bind(MusicVideo musicVideo) {
            textView.setText(musicVideo.getTitle());
            Glide.with(context)
                    .load(musicVideo.getImageUrl())
                    .placeholder(R.drawable.woman)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(musicVideo.getVideoUrl()));
                context.startActivity(intent);
            });
        }
    }
}