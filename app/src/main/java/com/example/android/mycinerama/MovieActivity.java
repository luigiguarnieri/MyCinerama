package com.example.android.mycinerama;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.mycinerama.models.Movie;

public class MovieActivity extends AppCompatActivity implements MovieActivityFragment.Callback{

    /**
     * Tag for the log messages
     */
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieActivity.class.getName();

    /**
     * Boolean to adapt fragments visualization if screen size width is
     * min 600dpi
     */
    private boolean mDoublePane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // If app is visualized on a tablet, both MovieActivityFragment
        // and MovieDetailFragment are shown on the main screen.
        if (findViewById(R.id.detail_root_layout) != null) {
            mDoublePane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_root_layout, new MovieDetailFragment(),
                                MovieDetailFragment.TAG)
                        .commit();
            }
        } else {
            mDoublePane = false;
        }

    }

    // Method to send "movie" object via Intent to MovieDetailFragment.
    @Override
    public void onItemSelected(Movie movie) {
        if (mDoublePane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE_DETAILS, movie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_root_layout, fragment, MovieDetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailFragment.MOVIE_DETAILS, movie);
            startActivity(intent);
        }
    }
}
