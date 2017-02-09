package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.data.cp.FavouriteContentProvider;
import com.example.android.popularmovies.data.cp.MovieColumns;

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

import static com.example.android.popularmovies.MovieDetailActivity.mMovie;

/**
 * Created by Vamsi on 2/9/2017.
 */

public class MovieLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    MovieAdapter mAdapter;
    Context mContext;

    public MovieLoader(MovieAdapter adapter, Context context){
        mAdapter = adapter;
        mContext = context;
    }
    /*
    * Implementation of loader callback methods.
    * */
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<Movie>>(mContext) {
            ArrayList<Movie> mData = new ArrayList<>();
            @Override
            protected void onStartLoading() {
                if(mData.isEmpty())
                    forceLoad();
                else
                    deliverResult(mData);
            }
            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mData = data;
                super.deliverResult(data);
            }

            //Helper: get data from a network connection
            private String fetchData(String... strings) throws IOException {
                String type;
                String id;
                String BASE_URI = "https://api.themoviedb.org/3/movie";
                String API_KEY = "api_key";
                String LANGUAGE = "language";
                String PAGE = "page";

                //https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US
                //https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US
                String language = "en-US";

                String jsonStr="";
                Uri uri = Uri.parse(BASE_URI);
                //The uri changes with the number of arguments to the method
                if(strings.length == 1){
                    type = strings[0];
                    uri = uri.buildUpon()
                            .appendPath(type).build();
                }else if(strings.length == 2){
                    type = strings[0];
                    id = strings[1];
                    uri = uri.buildUpon()
                            .appendPath(id)
                            .appendPath(type).build();
                }

                uri = uri.buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(LANGUAGE, language)
                        .appendQueryParameter(PAGE, "1")
                        .build();

                HttpURLConnection urlConnection;
                BufferedReader reader;

                URL url = new URL(uri.toString());
//                Log.e("MovieFragment:fetchdata", uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
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
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();

                return jsonStr;
            }
            //Helper: Json string is passed and relevant list object is retured
            private ArrayList<Movie> getList(String forecastJsonStr) throws IOException {
                //Result objects
                ArrayList<Movie> results = new ArrayList<>();
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(forecastJsonStr);
                    String RESULTS = "results";
                    String CONTENT = "content";
                    JSONArray moviesArray = jsonObject.getJSONArray(RESULTS);

                    for (int i = 0; i < moviesArray.length(); i++) {
                        JSONObject movie = moviesArray.getJSONObject(i);

                        //The release date using the java.util.Date class
                        //This is passed to the constructor of Movie class
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(movie.getString("release_date"));
                        Movie movieObject = new Movie(movie.getString("id"), movie.getString("original_title"), movie.getString("backdrop_path"),
                                movie.getString("overview"),
                                movie.getDouble("vote_average"), date,
                                movie.getDouble("popularity"));

                        results.add(movieObject);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return results;
            }

            //The method that executes in a new thread
            @Override
            public ArrayList<Movie> loadInBackground() {
                ArrayList<Movie> results = new ArrayList<>();
                String sortBy = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString(mContext.getString(R.string.pref_sort_key), mContext.getResources().getString(R.string.pref_sort_order_default));

                //If favourites is chosen, we go to te database fetch the data and render as required
                if(sortBy.equals(mContext.getString(R.string.favorites))){
                    ContentResolver resolver = mContext.getContentResolver();
                    Cursor cursor = resolver.query(FavouriteContentProvider.Lists.LISTS, null,
                            null, null, null);
                    while(cursor != null && cursor.moveToNext()){
                        Movie movie = new Movie(String.valueOf(cursor.getInt(cursor.getColumnIndex(MovieColumns._ID))),
                                cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE)),
                                cursor.getString(cursor.getColumnIndex(MovieColumns.IMAGE_URI)));
                        results.add(movie);
                    }
                    if(cursor != null)
                        cursor.close();
                    return results;
                }

                try {
                    //Other 2 cases we go to network and get the data.
                    String jsonStr = fetchData(sortBy);//helper call to get data from network
                    results = getList(jsonStr);//helper call to get relevant data from the network response
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (data != null) {
            mAdapter.clear();
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
}
