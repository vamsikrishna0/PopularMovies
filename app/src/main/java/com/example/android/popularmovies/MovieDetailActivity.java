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
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.adapters.ReviewAdapter;
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

import static android.R.attr.id;

public class MovieDetailActivity extends AppCompatActivity implements LoaderCallbacks<MovieDetailActivity.MovieDetail> {
    final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w185/";
    final String DETAIL = "detail";
    public TextView mOriginalTitle;
    public TextView mOverview;
    public TextView mVoteAverage;
    public TextView mReleaseDate;
    public ListView mReviewListView;
    public ListView mTrailerListView;
    public ImageView mPoster;
    public ReviewAdapter mReviewAdapter;
    public TrailerAdapter mTrailerAdapter;
    public static Movie mMovie;
    public static final String ID_KEY = "id";
    public static int LOADER_ID = 356;

    public static final String SAVEDINSTANCE_MOVIE = "movie";
    public static final String SAVEDINSTANCE_TRAILER = "trailers";
    public static final String SAVEDINSTANCE_REVIEWS = "reviews";
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
        mReviewListView = (ListView) findViewById(R.id.detail_reviews_listview);
        mTrailerListView = (ListView) findViewById(R.id.detail_trailers_listview);

        //Check for savedInstanceState
        if (savedInstanceState == null) {
            mMovie = (Movie) this.getIntent().getExtras().getSerializable(DETAIL);
            Bundle queryBundle = new Bundle();
            queryBundle.putString(ID_KEY, mMovie.getId());
            getSupportLoaderManager().initLoader(LOADER_ID, queryBundle, this);

            mReviewAdapter = new ReviewAdapter(this, new ArrayList<String>());
            mTrailerAdapter = new TrailerAdapter(this, new ArrayList<Trailer>());

        } else {
            mMovie = (Movie) savedInstanceState.getSerializable(SAVEDINSTANCE_MOVIE);
            mReviewAdapter = (ReviewAdapter) savedInstanceState.getSerializable(SAVEDINSTANCE_REVIEWS);
            mTrailerAdapter = (TrailerAdapter) savedInstanceState.getSerializable(SAVEDINSTANCE_TRAILER);
        }
        TextView textView = new TextView(this);

        //Updating data
        updateUI();

        //Getting the FAB and setting an onClickListener
        setUpFab();
    }

    private void updateUI(){
        mReviewListView.setAdapter(mReviewAdapter);
        mTrailerListView.setAdapter(mTrailerAdapter);

        Picasso.with(this).load(IMAGE_BASE_URI + mMovie.getImageUri()).resize(700, 400).into(mPoster);
        mOriginalTitle.setText("Original Title: " + mMovie.getOriginalTitle());
        mOverview.setText("Overview: " + mMovie.getOverview());
        mVoteAverage.setText("User Rating: " + String.valueOf(mMovie.getVoteAverage()));
        mReleaseDate.setText("Date: " + mMovie.getReadableDateString());
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
        outState.putSerializable(SAVEDINSTANCE_REVIEWS, mReviewAdapter);
        outState.putSerializable(SAVEDINSTANCE_TRAILER, mTrailerAdapter);
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

                //api_key=29b5c38bd9cb290af42a02bf51e15193&language=en-US&sort_by=popularity.desc&page=1
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
            mReviewAdapter.clear();
            mTrailerAdapter.clear();
            mReviewAdapter.addAll(data.getReviews());
            mTrailerAdapter.addAll(data.getTrailers());
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
