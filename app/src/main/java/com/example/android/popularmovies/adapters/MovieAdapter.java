package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.MovieDetailActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> implements Serializable {

    private final static String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w185/";
    private static final String DETAIL = "detail";

    public MovieAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, parent, false);
        }
        final Movie movie = getItem(position);
        final ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_main);

        Picasso.with(getContext()).load(IMAGE_BASE_URI + movie.getImageUri()).resize(350, 280).into(thumbnail);
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra(DETAIL, movie);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
