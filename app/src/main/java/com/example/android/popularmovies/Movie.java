package com.example.android.popularmovies;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {
    private String originalTitle;
    private String imageUri;
    private String overview; // Plot Synopsis
    private double voteAverage;
    private Date releaseDate;
    private double popularity;

    public Movie(String originalTitle, String imageUri, String overview, double voteAverage, Date releaseDate, double popularity) {
        this.originalTitle = originalTitle;
        this.imageUri = imageUri;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public double getPopularity() {
        return popularity;
    }
}
