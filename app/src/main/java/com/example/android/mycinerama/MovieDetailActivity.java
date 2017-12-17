package com.example.android.mycinerama;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    private MovieDetailFragment mRetainedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movie_detail);

//        // find the retained fragment on activity restarts
//        FragmentManager fm = getSupportFragmentManager();
//        mRetainedFragment = (MovieDetailFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
//        Log.e(TAG, "FRAGMENT RETAINED");


        if (savedInstanceState == null) {
        // create the fragment and data the first time
//        if (mRetainedFragment == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE_DETAILS,
                    getIntent().getParcelableExtra(MovieDetailFragment.MOVIE_DETAILS));

            mRetainedFragment = new MovieDetailFragment();
            Log.e(TAG, "NEW FRAGMENT");
            mRetainedFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_root_layout, mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
//                    .add(R.id.detail_root_layout, fragment)
//                    .commit();
        }
    }
}
