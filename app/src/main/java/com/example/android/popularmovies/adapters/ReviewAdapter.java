package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Trailer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vamsi on 2/8/2017.
 */

public class ReviewAdapter extends ArrayAdapter<String> implements Serializable {
    public ReviewAdapter(Context context, List<String> object) {
        super(context, 0, object);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_trailers, parent, false);
        }
        final String review = getItem(position);
        final TextView trailerText = (TextView) convertView.findViewById(R.id.detail_trailer);
        trailerText.setText(review);
        return convertView;
    }
}
