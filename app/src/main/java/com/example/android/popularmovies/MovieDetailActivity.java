package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.data.cp.FavouriteContentProvider;
import com.example.android.popularmovies.data.cp.MovieColumns;
import com.squareup.picasso.Picasso;

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

public class MovieDetailActivity extends AppCompatActivity implements LoaderCallbacks<MovieDetailActivity.MovieDetail> {
    final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w185/";
    final String DETAIL = "detail";
    public TextView mOriginalTitle;
    public TextView mOverview;
    public TextView mVoteAverage;
    public TextView mReleaseDate;
    public LinearLayout mTrailersView;
    public LinearLayout mReviewsView;
    public ImageView mPoster;
    public static Movie mMovie;
    public static final String ID_KEY = "id";
    public static int LOADER_ID = 356;

    public static final String SAVEDINSTANCE_MOVIE = "movie";
    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Getting the UI elements
        mOriginalTitle = (TextView) findViewById(R.id.detail_original_title);
        mOverview = (TextView) findViewById(R.id.detail_overview);
        mVoteAverage = (TextView) findViewById(R.id.detail_vote_average);
        mReleaseDate = (TextView) findViewById(R.id.detail_release_date);
        mPoster = (ImageView) findViewById(R.id.detail_poster);
        mTrailersView = (LinearLayout) findViewById(R.id.detail_trailers_ll);
        mReviewsView = (LinearLayout) findViewById(R.id.detail_reviews_ll);

        //Check for savedInstanceState
        if (savedInstanceState == null) {
            mMovie = (Movie) this.getIntent().getExtras().getSerializable(DETAIL);
            Bundle queryBundle = new Bundle();
            queryBundle.putString(ID_KEY, mMovie.getId());
            getSupportLoaderManager().initLoader(LOADER_ID, queryBundle, this);

        } else {
            mMovie = (Movie) savedInstanceState.getSerializable(SAVEDINSTANCE_MOVIE);
            updateUI();        //Updating data
        }
        //Getting the FAB and setting an onClickListener
        setUpFab();
    }

    private void updateUI(){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for(String review: mMovie.getReviews()){
            TextView textView = new TextView(this);
            textView.setText(review);
            textView.setLayoutParams(layoutParams);
            Log.v(TAG, "Reviews set");
            mReviewsView.addView(textView);
        }
        for(final Trailer trailer: mMovie.getTrailers()){
            TextView trailerText = new TextView(this);
            trailerText.setText(trailer.getName());
            trailerText.setTextSize(26);
            trailerText.setTextColor(getResources().getColor(R.color.colorPrimary));
            trailerText.setPadding(20, 0, 0, 4);
            trailerText.setLayoutParams(layoutParams);
            Log.v(TAG, trailer.getName());
            trailerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = trailer.getId();
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                    if(appIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(appIntent);
                    }

                }
            });
            mTrailersView.addView(trailerText);
        }
        Picasso.with(this).load(IMAGE_BASE_URI + mMovie.getImageUri()).resize(700, 400).into(mPoster);
        String text = getString(R.string.original_title) + mMovie.getOriginalTitle();
        mOriginalTitle.setText(text);
        text = getString(R.string.overview) + mMovie.getOverview();
        mOverview.setText(text);
        text = getString(R.string.user_rating) + mMovie.getVoteAverage();
        mVoteAverage.setText(text);
        text = getString(R.string.date) + mMovie.getReleaseDate();
        mReleaseDate.setText(text);
    }

    private void setUpFab(){
        final FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(FavouriteContentProvider.Lists.withId(Long.parseLong(mMovie.getId())), null,
                MovieColumns._ID + " = " + Integer.parseInt(mMovie.getId()), null, null);
        setFab(fabButton, cursor == null);
        if (cursor != null){
            Log.v(TAG, cursor.getColumnName(0));
            cursor.close();
        }

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //True - already favourite
                //False - not fav yet
                ContentResolver resolver = getContentResolver();
                if ((boolean) fabButton.getTag()) {
                    int p = resolver.delete(FavouriteContentProvider.Lists.withId(Long.parseLong(mMovie.getId())),
                            MovieColumns._ID + " = " + Integer.parseInt(mMovie.getId()), null);
                    if (p > 0)
                        Log.v(TAG, "deleted");
                    setFab(fabButton, false);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieColumns._ID, Integer.parseInt(mMovie.getId()));
                    cv.put(MovieColumns.TITLE, mMovie.getOriginalTitle());
                    Uri p = resolver.insert(FavouriteContentProvider.Lists.withId(Long.parseLong(mMovie.getId())), cv);
                    if (p != null)
                        Log.v(TAG, "inserted");
                    setFab(fabButton, true);
                }
            }
        });
    }
    public static void setFab(FloatingActionButton fab, boolean status) {
        if (status) {
            fab.setImageResource(R.drawable.ic_star_black);

        } else {
            fab.setImageResource(R.drawable.ic_star_border_black);
        }
        fab.setTag(status);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVEDINSTANCE_MOVIE, mMovie);
    }

    /*
    * Implementation of loader callback methods:
    * Get the data from network and set it to local variables
    * */
    //Fetching data for detail
    @Override
    public Loader<MovieDetail> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDetail>(this) {
            MovieDetail mData;

            @Override
            protected void onStartLoading() {
                if (mData == null) {
                    mData = new MovieDetail();
                    forceLoad();
                    Log.v(TAG, "loaded data");
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

                HttpURLConnection urlConnection;
                BufferedReader reader;

                URL url = new URL(uri.toString());
                Log.e("MovieFragment:fetchdata", uri.toString());

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
                    Log.v("MovieFragment:getList", reviewsStr);
                    JSONArray reviewsArray = new JSONObject(reviewsStr).getJSONArray(RESULTS);
                    for (int j = 0; j < reviewsArray.length(); j++) {
                        JSONObject review = reviewsArray.getJSONObject(j);
                        if (review.has(CONTENT)) {
                            reviews.add(review.getString(CONTENT));
//                                Log.v("MovieFragment:getListI", review.getString(CONTENT));
                        }
                    }

                    //Getting the trailers data with an api call
                    ArrayList<Trailer> trailers = new ArrayList<>();
                    String trailersStr = fetchData(TRAILERS, id);
                    Log.v("MovieFragment:getList", trailersStr);
                    JSONArray trailersArray = new JSONObject(trailersStr).getJSONArray("youtube");
                    for (int j = 0; j < trailersArray.length(); j++) {
                        JSONObject tr = trailersArray.getJSONObject(j);
                        trailers.add(new Trailer(tr.getString("source"), tr.getString("name")));
//                            Log.v("MovieFragment:getList", tr.getString("name"));

                    }
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
            Log.v(TAG, "Movie data set");
            updateUI();        //Updating data
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
