package com.example.android.mycinerama;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mycinerama.data.MovieContract;
import com.example.android.mycinerama.utilities.ReviewAsyncTaskLoader;
import com.example.android.mycinerama.utilities.TrailerAsyncTaskLoader;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MovieDetailActivity.class.getName();

    private Movie movie;

    private int movieId;

    private String thumbnail;
    private String title;
    private String overview;
    private String backdrop;
    private String release;
    private double vote;

    private List<Review> reviewArrayList;
    private ReviewAdapter mReviewAdapter;
    private List<Trailer> trailerArrayList;
    private TrailerAdapter mTrailerAdapter;

    @BindView(R.id.review_recycler_view) RecyclerView mReviewRecyclerView;

    @BindView(R.id.trailer_recycler_view) RecyclerView mTrailerRecyclerView;

    @BindView(R.id.favorite_button) FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_detail_movie_title) TextView movieTitleTextView;
    @BindView(R.id.tv_detail_movie_rating) TextView movieRatingTextView;
    @BindView(R.id.tv_detail_movie_plot) TextView moviePlotTextView;
    @BindView(R.id.tv_detail_movie_release_year) TextView movieReleaseYearTextView;
    @BindView(R.id.iv_detail_movie_backdrop) ImageView movieBackdropImageView;
    @BindView(R.id.iv_detail_movie_poster) ImageView moviePosterImageView;
    @BindView(R.id.review_movie_container)
    CardView reviewContainerCardView;

    @BindView(R.id.review_section_title) TextView reviewSectionTitle;
    @BindView(R.id.trailer_section_title) TextView trailerSectionTitle;

    @BindView(R.id.trailer_movie_container)
    CardView trailerContainerCardView;

    @BindView(R.id.detail_root_layout)
    CoordinatorLayout coordinatorLayout;

    private static final int REVIEW_LOADER_ID = 33;
    private static final int TRAILER_LOADER_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        reviewArrayList = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(reviewArrayList, MovieDetailActivity.this);
        LinearLayoutManager mReviewLayoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        trailerArrayList = new ArrayList<>();
        mTrailerAdapter = new TrailerAdapter(trailerArrayList, MovieDetailActivity.this);
        LinearLayoutManager mTrailerLayoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(mTrailerLayoutManager);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);



        if(savedInstanceState != null){
            movie = savedInstanceState.getParcelable("movie_detail");
        }

        movie = getIntent().getExtras().getParcelable("movie");

        movieId = movie.getmMovieID();
        Log.e(LOG_TAG, "MovieID is " + movieId);

        Log.e(LOG_TAG, movie.getmMovieTitle() + movie.getmMovieRating() +
        movie.getmMoviePoster() + movie.getmMovieBackdrop()  + movie.getmMovieID() +
        movie.getmMoviePlot() + movie.getmMovieDate());

            // Find the TextView in the activity_movie_detail layout with the ID version_name
            // and set the title of the movie
            title = movie.getmMovieTitle();
            movieTitleTextView.setText(title);

            // Find the TextView in the activity_movie_detail layout with the ID version_name
            // and set rating of the movie
            vote = movie.getmMovieRating();
            movieRatingTextView.setText(String.valueOf(vote));

            // Find the TextView in the activity_movie_detail layout with the ID version_name
            // and set the plot synopsis of the movie
            overview = movie.getmMoviePlot();
            moviePlotTextView.setText(overview);

            // Find the TextView in the activity_movie_detail layout with the ID version_name
            // and set the release year of the movie
            release = movie.getmMovieDate();
            try {
                movieReleaseYearTextView.setText(MovieAdapter.formatReleaseDate(release));
            } catch (ParseException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                e.printStackTrace();
                Log.e(LOG_TAG, getString(R.string.error_formatting_movie_release_date), e.getCause());
            }

            thumbnail = movie.getmMoviePoster();
            backdrop = movie.getmMovieBackdrop();
            // Check if a retrieved an image from themoviedb.org API and it's no empty
            if (backdrop != null && !backdrop.isEmpty()) {
                // Use the Picasso library to download movie poster and set it in the ImageView
                Picasso.with(getBaseContext()).load((backdrop)).into(movieBackdropImageView);
            } else {
                // If no image is retrieved, it's set a dummy movie poster in the ImageView
                movieBackdropImageView.setImageResource(R.drawable.image_view_empty_grid);
            }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Use the Picasso library to download movie poster and set it in the ImageView
            Picasso.with(getBaseContext()).load((thumbnail)).into(moviePosterImageView);
        } else {
            // If no image is retrieved, it's set a dummy movie poster in the ImageView
            movieBackdropImageView.setImageResource(R.drawable.image_view_empty_grid);
        }

        if(checkFavoriteMovie(movieId))
            floatingActionButton.setImageResource(R.drawable.ic_star_full);
        else
            floatingActionButton.setImageResource(R.drawable.ic_star_empty);

        mTrailerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Trailer trailer = trailerArrayList.get(position);
                        youTubeTrailerVideo(MovieDetailActivity.this, trailer.getmTrailerKey());
                    }
        }));

        reviewSectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReviewRecyclerView.getVisibility() == View.GONE){
                    mReviewRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mReviewRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        trailerSectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrailerRecyclerView.getVisibility() == View.GONE){
                    mTrailerRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mTrailerRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        if (deviceIsConnected()) {
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, mLoaderCallbackReview);
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, mLoaderCallbackTrailer);
        } else {
            /* TODO Implement a way to make this check unnecessary, saving reviews to db.
             * In this way it'll be necessary only to make mReviewRecyclerView.setVisibility(View.INVISIBLE)
             * for the new reviews that are retrieved from internet.
             */
            reviewContainerCardView.setVisibility(View.INVISIBLE);
            trailerContainerCardView.setVisibility(View.INVISIBLE);
        }
        reviewArrayList.clear();
        trailerArrayList.clear();
        getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID,null,mLoaderCallbackReview).forceLoad();
        getSupportLoaderManager().restartLoader(TRAILER_LOADER_ID, null, mLoaderCallbackTrailer).forceLoad();

        }

    @OnClick(R.id.favorite_button)
    void likeMovie(View view){
        if(checkFavoriteMovie(movieId)){
            removeFavoriteMovie(movieId);
            floatingActionButton.setImageResource(R.drawable.ic_star_empty);
            Snackbar.make(coordinatorLayout, "Removed from your favorite movies", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else {
            ContentValues testValues = new ContentValues();
            testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, thumbnail);
            testValues.put(MovieContract.MovieEntry.COLUMN_PLOT, overview);
            testValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, backdrop);
            testValues.put(MovieContract.MovieEntry.COLUMN_RATING, vote);
            testValues.put(MovieContract.MovieEntry.COLUMN_DATE, release);
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,testValues);
            if(uri!=null){
                floatingActionButton.setImageResource(R.drawable.ic_star_full);
                Snackbar.make(coordinatorLayout, "Added to your favorite movies", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void removeFavoriteMovie(long id){
        int movieDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
    private boolean checkFavoriteMovie(int id){
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }



    private LoaderManager.LoaderCallbacks<List<Review>> mLoaderCallbackReview =
            new LoaderManager.LoaderCallbacks<List<Review>>() {

        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            Log.e(LOG_TAG, "MovieID is " + movieId);
            return new ReviewAsyncTaskLoader(MovieDetailActivity.this, movieId);
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            reviewArrayList = data;
            if (data != null && !data.isEmpty()) {
                mReviewAdapter = new ReviewAdapter(data, MovieDetailActivity.this);
                mReviewRecyclerView.setAdapter(mReviewAdapter);
//            reviewContentTextView.setVisibility(View.INVISIBLE);
//            showMoviesDataView();
//            mLayoutManager.onRestoreInstanceState(mListState);
            } else {
                // If there is a valid grid of {@link Book}s, then add them to the adapter's
                // data set. This will trigger the GridView to update.
                reviewContainerCardView.setVisibility(View.INVISIBLE);
//            reviewContentTextView.setVisibility(View.VISIBLE);
//            mReviewRecyclerView.setVisibility(View.INVISIBLE);
//            reviewContentTextView.setText("No review to show!");
            }

        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<Trailer>> mLoaderCallbackTrailer =
            new LoaderManager.LoaderCallbacks<List<Trailer>>() {
                @Override
                public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
                    Log.e(LOG_TAG, "MovieID is " + movieId);
                    return new TrailerAsyncTaskLoader(MovieDetailActivity.this, movieId);
                }

                @Override
                public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
                    trailerArrayList = data;
                    if (data != null && !data.isEmpty()) {
                        mTrailerAdapter = new TrailerAdapter(data, MovieDetailActivity.this);
                        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
                    } else {
                        // If there is a valid grid of {@link Book}s, then add them to the adapter's
                        // data set. This will trigger the GridView to update.
                        trailerContainerCardView.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onLoaderReset(Loader<List<Trailer>> loader) {

                }
            };


    // Helper method to verify if network connectivity is available
    public boolean deviceIsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void youTubeTrailerVideo(Context context, String trailerKey){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
