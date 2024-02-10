package com.luka.anidroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Anime implements Serializable, Comparable<Anime>{
    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private boolean isFavourite;
    private int id;
    private String url;
    private double averageScore;
    private String duration;
    private Date startDate;
    private Date endDate;
    private int episodes;
    private int popularity;
    private int seasonYear;
    private String title;
    private String titleNative;
    private String titleRomaji;
    private String format;
    private String season;
    private String status;
    private String type;
    private List<String> genres;
    private List<String> tags;
    private String imageUrl;
    private String description;
    private boolean isAiring;
    private String trailerUrl;
    private String broadcastDay;

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getBroadcastDay() {
        return broadcastDay;
    }

    public void setBroadcastDay(String broadcastDay) {
        this.broadcastDay = broadcastDay;
    }

    public boolean isAiring() {
        return isAiring;
    }

    public void setAiring(boolean airing) {
        isAiring = airing;
    }

    public Anime() {
    }

    public Anime(boolean isFavourite, int id, int averageScore, String duration, Date startDate, Date endDate, int episodes, int popularity, int seasonYear, String title, String titleNative, String titleRomaji, String format, String season, String status, String type, List<String> genres, List<String> tags, String imageUrl, String description) {
        this.isFavourite = isFavourite;
        this.id = id;
        this.averageScore = averageScore;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.episodes = episodes;
        this.popularity = popularity;
        this.seasonYear = seasonYear;
        this.title = title;
        this.titleNative = titleNative;
        this.titleRomaji = titleRomaji;
        this.format = format;
        this.season = season;
        this.status = status;
        this.type = type;
        this.genres = genres;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    protected Anime(Parcel in) {
        id = in.readInt();
        url = in.readString();
        averageScore = in.readInt();
        duration = in.readString();
        episodes = in.readInt();
        popularity = in.readInt();
        seasonYear = in.readInt();
        title = in.readString();
        titleNative = in.readString();
        titleRomaji = in.readString();
        format = in.readString();
        season = in.readString();
        status = in.readString();
        type = in.readString();
        genres = in.createStringArrayList();
        tags = in.createStringArrayList();
        imageUrl = in.readString();
        description = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getSeasonYear() {
        return seasonYear;
    }

    public void setSeasonYear(int seasonYear) {
        this.seasonYear = seasonYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleNative() {
        return titleNative;
    }

    public void setTitleNative(String titleNative) {
        this.titleNative = titleNative;
    }

    public String getTitleRomaji() {
        return titleRomaji;
    }

    public void setTitleRomaji(String titleRomaji) {
        this.titleRomaji = titleRomaji;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Anime o) {
        return this.getId() - o.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anime anime = (Anime) o;
        return id == anime.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}