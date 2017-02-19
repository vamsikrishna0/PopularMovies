package com.example.android.popularmovies.data;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.MovieDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.MovieDetailActivity.ID_KEY;
import static com.example.android.popularmovies.MovieDetailActivity.TAG;

/**
 * Created by Vamsi on 2/17/2017.
 */

public class DetailLoader implements LoaderManager.LoaderCallbacks<DetailLoader.MovieDetail> {
    Movie mMovie;
    MovieDetailActivity mContext;

    public DetailLoader(Movie movie, MovieDetailActivity context) {
        mMovie = movie;
        mContext = context;
    }

    /*
   * Implementation of loader callback methods:
   * Get the data from network and set it to local variables
   * */
    //Fetching data for detail
    @Override
    public Loader<MovieDetail> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDetail>(mContext) {
            MovieDetail mData;

            @Override
            protected void onStartLoading() {
                if (mData == null) {
                    mData = new MovieDetail();
                    forceLoad();
                } else
                    deliverResult(mData);

            }

            @Override
            public void deliverResult(MovieDetail data) {
                mData = data;
                super.deliverResult(data);
            }

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

                String jsonStr;
                Uri uri = Uri.parse(BASE_URI);
                //Build URL based on number of arguments passed
                if (strings.length == 1) {
                    type = strings[0];
                    uri = uri.buildUpon()
                            .appendPath(type).build();
                } else if (strings.length == 2) {
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

                //Create the Http connection to get the data
                HttpURLConnection urlConnection;
                BufferedReader reader;

                URL url = new URL(uri.toString());

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

            @Override
            public MovieDetail loadInBackground() {
                if (args == null)
                    return null;

                String RESULTS = "results";
                String CONTENT = "content";
                String REVIEWS = "reviews";
                String TRAILERS = "trailers";

                String id = args.getString(ID_KEY);

                try {
                    //Getting the reviews with an api call
                    ArrayList<String> reviews = new ArrayList<>();
                    String reviewsStr = fetchData(REVIEWS, id);
                    JSONArray reviewsArray = new JSONObject(reviewsStr).getJSONArray(RESULTS);
                    for (int j = 0; j < reviewsArray.length(); j++) {
                        JSONObject review = reviewsArray.getJSONObject(j);
                        if (review.has(CONTENT)) {
                            reviews.add(review.getString(CONTENT));
                        }
                    }

                    //Getting the trailers data with an api call
                    ArrayList<Trailer> trailers = new ArrayList<>();
                    String trailersStr = fetchData(TRAILERS, id);
                    JSONArray trailersArray = new JSONObject(trailersStr).getJSONArray("youtube");
                    for (int j = 0; j < trailersArray.length(); j++) {
                        JSONObject tr = trailersArray.getJSONObject(j);
                        trailers.add(new Trailer(tr.getString("source"), tr.getString("name")));
                    }

                    //Setting mData object.
                    mData.setReviews(reviews);
                    mData.setTrailers(trailers);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return mData;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDetail> loader, MovieDetail data) {
        if (data != null) {
            mMovie.setTrailers(data.getTrailers());
            mMovie.setReviews(data.getReviews());
            mContext.updateUI();
            Log.v(TAG, "data is set");
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieDetail> loader) {

    }

    class MovieDetail {
        ArrayList<Trailer> trailers;
        ArrayList<String> reviews;

        void setTrailers(ArrayList<Trailer> trailers) {
            this.trailers = trailers;
        }

        void setReviews(ArrayList<String> reviews) {
            this.reviews = reviews;
        }

        ArrayList<String> getReviews() {
            return reviews;
        }

        ArrayList<Trailer> getTrailers() {
            return trailers;
        }
    }
}
