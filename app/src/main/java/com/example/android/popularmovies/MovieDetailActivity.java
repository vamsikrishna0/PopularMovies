package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.R.attr.thumbnail;
import static java.lang.System.load;

public class MovieDetailActivity extends AppCompatActivity {
    final String imageBaseUri = "http://image.tmdb.org/t/p/w185/";
    final String DETAIL = "detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        TextView originalTitle = (TextView) findViewById(R.id.detail_original_title);
        TextView overview = (TextView) findViewById(R.id.detail_overview);
        TextView voteAverage = (TextView) findViewById(R.id.detail_vote_average);
        TextView releaseDate = (TextView) findViewById(R.id.detail_release_date);
        ImageView poster = (ImageView) findViewById(R.id.detail_poster);
        Movie movie = (Movie) this.getIntent().getExtras().getSerializable(DETAIL);
        Picasso.with(this).load(imageBaseUri + movie.getImageUri()).resize(700, 400).into(poster);
        originalTitle.setText("Original Title: " + movie.getOriginalTitle());
        overview.setText("Overview: " + movie.getOverview());
        voteAverage.setText("User Rating: " + String.valueOf(movie.getVoteAverage()));
        releaseDate.setText("Date: " + movie.getReadableDateString());
    }
}
