package com.example.android.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    MovieAdapter mAdapter;
    private final String ADAPTER = "adapter";
    private static final int LOADER_ID = 56;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PreferenceManager
                .getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        if(savedInstanceState == null){
            mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
            updateMovies();
        }
        else
            mAdapter = (MovieAdapter)savedInstanceState.getSerializable(ADAPTER);
    }

    public void updateMovies() {
        MovieLoader loader = new MovieLoader(mAdapter, this.getActivity());
        getLoaderManager().restartLoader(LOADER_ID, null, loader);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_sort_order, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ADAPTER, mAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView grid = (GridView) returnView.findViewById(R.id.movie_image_gridview);
        grid.setAdapter(mAdapter);

        return returnView;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        Log.v("Movie Fragment", "shared preference changed");
        updateMovies();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


}

