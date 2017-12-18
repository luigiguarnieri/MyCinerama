package com.example.android.mycinerama;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private static final String TAG_MOVIE_DETAIL_FRAGMENT = "MovieDetailFragment";

    @SuppressWarnings("FieldCanBeLocal")
    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movie_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE_DETAILS,
                    getIntent().getParcelableExtra(MovieDetailFragment.MOVIE_DETAILS));

            movieDetailFragment = new MovieDetailFragment();
            Log.e(LOG_TAG, "NEW FRAGMENT");
            movieDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_root_layout, movieDetailFragment, TAG_MOVIE_DETAIL_FRAGMENT).commit();
        }
    }
}
