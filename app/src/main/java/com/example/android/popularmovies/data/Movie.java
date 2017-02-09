package com.example.android.popularmovies.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class Movie implements Serializable {
    private String originalTitle;
    private String imageUri;
    private String overview; // Plot Synopsis
    private double voteAverage;
    private Date releaseDate;
    private double popularity;
    private String id;
    private ArrayList<Trailer> trailers;
    private ArrayList<String> reviews;

    public Movie(String id, String originalTitle, String imageUri, String overview, double voteAverage, Date releaseDate, double popularity) {
        this.originalTitle = originalTitle;
        this.imageUri = imageUri;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.id = id;
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}
