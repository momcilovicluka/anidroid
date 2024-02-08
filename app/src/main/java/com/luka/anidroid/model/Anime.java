package com.luka.anidroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;


public class Anime implements Parcelable {
    private int id;
    private String url;
    private double averageScore;
    private int duration;
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

    public Anime() {
    }

    public Anime(int id, int averageScore, int duration, Date startDate, Date endDate, int episodes, int popularity, int seasonYear, String title, String titleNative, String titleRomaji, String format, String season, String status, String type, List<String> genres, List<String> tags, String imageUrl, String description) {
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
        duration = in.readInt();
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

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeDouble(averageScore);
    }
}