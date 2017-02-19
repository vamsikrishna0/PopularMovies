package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.DetailLoader;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.data.cp.FavouriteContentProvider;
import com.example.android.popularmovies.data.cp.MovieColumns;
import com.squareup.picasso.Picasso;
/*
* Data fetching is done in DetailLoader class.
* */
public class MovieDetailActivity extends AppCompatActivity {
    final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w185/";
    final String DETAIL = "detail";
    public TextView mOriginalTitle;
    public TextView mOverview;
    public TextView mVoteAverage;
    public TextView mReleaseDate;
    public LinearLayout mTrailersView;
    public LinearLayout mReviewsView;
    public ImageView mPoster;
    public Movie mMovie;
    public static final String ID_KEY = "id";
    public static int LOADER_ID = 356;

    public static final String SAVEDINSTANCE_MOVIE = "movie";
    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVEDINSTANCE_MOVIE, mMovie);
    }

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
            DetailLoader loader = new DetailLoader(mMovie, this);
            LoaderManager manager = getSupportLoaderManager();
            manager.initLoader(LOADER_ID, queryBundle, loader);
        } else {
            mMovie = (Movie) savedInstanceState.getSerializable(SAVEDINSTANCE_MOVIE);
            updateUI();        //Updating data
        }
        //Getting the FAB and setting an onClickListener
        setUpFab();
    }

    //Update the Detail page UI, After the data is received
    public void updateUI(){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for(String review: mMovie.getReviews()){
            TextView textView = new TextView(this);
            textView.setText(review);
            textView.setLayoutParams(layoutParams);
            mReviewsView.addView(textView);
        }
        for(final Trailer trailer: mMovie.getTrailers()){
            TextView trailerText = new TextView(this);
            trailerText.setText(trailer.getName());
            trailerText.setTextSize(26);
            trailerText.setTextColor(getResources().getColor(R.color.primary));
            trailerText.setPadding(20, 0, 0, 4);
            trailerText.setLayoutParams(layoutParams);
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

    //Method to setup the Floating Action Button for Favourites
    private void setUpFab(){
        final FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(FavouriteContentProvider.Lists.withId(Long.parseLong(mMovie.getId())), null,
                null, null, null);
        boolean status = (cursor != null && cursor.moveToFirst());
        setFab(fabButton, status);
        fabButton.setTag(status);
        if(cursor != null)
            cursor.close();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //True - already favourite
                //False - not fav yet
                ContentResolver resolver = getContentResolver();
                if ((boolean) fabButton.getTag()) {
                    int p = resolver.delete(FavouriteContentProvider.Lists.withId(Long.parseLong(mMovie.getId())), null, null);
                    if (p > 0) {
                        setFab(fabButton, false);
                        fabButton.setTag(false);
                    }
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieColumns._ID, Integer.parseInt(mMovie.getId()));
                    cv.put(MovieColumns.TITLE, mMovie.getOriginalTitle());
                    cv.put(MovieColumns.IMAGE_URI, mMovie.getImageUri());
                    Uri p = resolver.insert(FavouriteContentProvider.Lists.LISTS, cv);

                    if (p != null){
                        setFab(fabButton, true);
                        fabButton.setTag(true);
                    }
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
    }
}
