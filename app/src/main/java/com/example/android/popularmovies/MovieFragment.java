package com.example.android.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment {

    MovieAdapter mAdapter;
    private final String ADAPTER = "adapter";
    String sortBy;
    boolean toggleSortOrder = true;
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Use Custom ArrayAdapter and a model object to store data for each movie entry(returned from the API)
        // Preserve the adapter by saving it to savedInstanceState by calling onSaveInstanceState

//        if(savedInstanceState != null){
//            mAdapter = (MovieAdapter) savedInstanceState.getSerializable(ADAPTER);
//        }else{

        sortBy = getString(R.string.pref_popularity);
        updateMovies(sortBy);

//        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_sort_order, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_sort_order && toggleSortOrder == false){
            toggleSortOrder = true;
            sortBy = getString(R.string.pref_popularity);
            updateMovies(sortBy);
        }else if(id == R.id.action_sort_order && toggleSortOrder == true){
            toggleSortOrder = false;
            sortBy = getString(R.string.pref_top_rated);
            updateMovies(sortBy);
        }

        GridView grid = (GridView) getActivity().findViewById(R.id.movie_image_gridview);
        grid.setAdapter(mAdapter);

        return true;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putSerializable(ADAPTER, mAdapter);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView grid = (GridView) returnView.findViewById(R.id.movie_image_gridview);
        grid.setAdapter(mAdapter);

        return returnView;
    }

    public void updateMovies(String sortBy) {
        mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        new FetchMovieTask().execute(sortBy);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            String BASE_URI = "https://api.themoviedb.org/3/movie";
            String API_KEY = "api_key";
            String LANGUAGE = "language";
            String PAGE = "page";
            //api_key=29b5c38bd9cb290af42a02bf51e15193&language=en-US&sort_by=popularity.desc&page=1
            //https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US
            //https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US

            String language = "en-US";

            Uri uri = Uri.parse(BASE_URI).buildUpon()
                    .appendPath(strings[0])
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(LANGUAGE, language)
                    .appendQueryParameter(PAGE, "1")
                    .build();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr;

            //Result objects
            ArrayList<Movie> results = new ArrayList<>();
            try {
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                String RESULTS = "results";
                JSONArray moviesArray = forecastJson.getJSONArray(RESULTS);

                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);

                    //The release date using the java.util.Date class
                    //This is passed to the constructor of Movie class
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(movie.getString("release_date"));
                    Movie movieObject = new Movie(movie.getString("original_title"), movie.getString("backdrop_path"),
                            movie.getString("overview"),
                            movie.getDouble("vote_average"), date,
                            movie.getDouble("popularity"));

                    results.add(movieObject);
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            if (movies != null) {
                mAdapter.clear();
                mAdapter.addAll(movies);
            }
        }
    }
}

