package com.example.android.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Trailer;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.resource;

public class TrailerAdapter extends ArrayAdapter<Trailer> implements Serializable {
    public TrailerAdapter(Context context, List<Trailer> object) {
        super(context, 0, object);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_trailers, parent, false);
        }
        final Trailer trailer = getItem(position);
        final TextView trailerText = (TextView) convertView.findViewById(R.id.detail_trailer);
        trailerText.setText(trailer.getName());
        trailerText.setTextSize(20);
        Log.v("TrailerAdapter: gView", trailer.getName());
        trailerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = trailer.getId();
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                if(appIntent.resolveActivity(getContext().getPackageManager()) != null){
                    getContext().startActivity(appIntent);
                }

//                try {
//                } catch (ActivityNotFoundException ex) {
//                    getContext().startActivity(webIntent);
//                }
            }
        });
        return convertView;
    }
}
