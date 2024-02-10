package com.luka.anidroid.model;

public class MusicVideo {
    private String title;
    private String imageUrl;
    private String videoUrl;

    public MusicVideo(String title, String imageUrl, String videoUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public MusicVideo() {

    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}