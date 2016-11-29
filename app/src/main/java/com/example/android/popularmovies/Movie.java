package com.example.android.popularmovies;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

    public String getReadableDateString() {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return shortenedDateFormat.format(releaseDate);
    }
}
