package com.example.android.mycinerama;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.mycinerama.data.MovieContract;
import com.example.android.mycinerama.utilities.MovieAsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>{

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MovieActivityFragment.class.getName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    private List<Movie> movieArrayList;
    private MovieAdapter mAdapter;
    private String SORT_ORDER = "sort";

    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String FAVORITE_MOVIES = "favorite";

    private static final int LOADER_ID = 22;


    private SharedPreferences mSharedPrefSettings;
    private SharedPreferences.Editor mSharedPrefEditor;



    public MovieActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_activity, container, false);
        ButterKnife.bind(this, rootView);

        movieArrayList = new ArrayList<>();
        mAdapter = new MovieAdapter(movieArrayList, getActivity());

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                        Movie movie = movieArrayList.get(position);
                        Intent movieIntent = new Intent(getActivity(), MovieDetailActivity.class);
                        movieIntent.putExtra("movie", movie);
                        startActivity(movieIntent);
            }
                }));

        mSharedPrefSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPrefEditor = mSharedPrefSettings.edit();
        mSharedPrefEditor.apply();


        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    private void populateMovieRecyclerView(){
        if (deviceIsConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if ((Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), POPULAR_MOVIES))
                        || (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), TOP_RATED_MOVIES))) {
                    getLoaderManager().initLoader(LOADER_ID, null, this);
                    Log.e(LOG_TAG, "OTHERS SHOWN - OK INTERNET");
                    movieArrayList.clear();
                    getLoaderManager().restartLoader(LOADER_ID,null,this).forceLoad();
                }
            } else {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                movieArrayList = getDataFromDB();
                mAdapter = new MovieAdapter(movieArrayList, getActivity());
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerView.setAdapter(mAdapter);
                Log.e(LOG_TAG, "FAVORITES SHOWN - OK INTERNET");
            }
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            movieArrayList = getDataFromDB();
            mAdapter = new MovieAdapter(movieArrayList, getActivity());
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(mAdapter);
            Log.e(LOG_TAG, "FAVORITES SHOWN - NO INTERNET");
            Toast.makeText(getActivity(), "  Your mobile device is offline!\nCheck your internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateMovieRecyclerView();
    }

    //Method to fetch data from DB
    private List<Movie> getDataFromDB() {
        movieArrayList = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor =
                resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            if (cursor.moveToFirst()) {
                do {
                    int movie_id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                    String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE));
                    String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
                    String backdrop = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));
                    double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
                    String plot = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT));

                    Movie movie = new Movie(movie_id, title, date, poster, backdrop, rating, plot);
                    movieArrayList.add(movie);
                } while (cursor.moveToNext());
            }
        } else if ((cursor != null) && (cursor.getCount() <= 0)) {
                Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                        "No favorite movie to show!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }


        if (cursor != null)
            cursor.close();

        return movieArrayList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_popular:
                if (deviceIsConnected()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), POPULAR_MOVIES)) {
                            Log.e(LOG_TAG, "equals to popular");
                            Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                                    "Showing Popular Movies Yet", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {
                            mSharedPrefEditor.putString(SORT_ORDER, POPULAR_MOVIES);
                            mSharedPrefEditor.apply();
                            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
                            Log.e(LOG_TAG, "BUTTON - POPULAR SHOWN - OK INTERNET");
                        }
                    }
                    item.setChecked(true);
                    return true;
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                            "No internet connection!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            case R.id.filter_top_rated:
                if (deviceIsConnected()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), TOP_RATED_MOVIES)) {
                            Log.e(LOG_TAG, "equals to top rated");
                            Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                                    "Showing Top Rated Movies Yet", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {
                            mSharedPrefEditor.putString(SORT_ORDER, TOP_RATED_MOVIES);
                            mSharedPrefEditor.apply();
                            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
                            Log.e(LOG_TAG, "BUTTON - TOP SHOWN - OK INTERNET");
                        }
                    }
                    item.setChecked(true);
                    return true;
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                            "No internet connection!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            case R.id.filter_favorite:
                if (deviceIsConnected()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), FAVORITE_MOVIES)) {
                            Log.e(LOG_TAG, "equals to favorites");
                            //                            Toast.makeText(getContext(), "Showing Top Rated Movies Yet", Toast.LENGTH_SHORT).show();
                            Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                                    "Showing Your Favorite Movies Yet", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {
                            movieArrayList.clear();
                            mSharedPrefEditor.putString(SORT_ORDER, FAVORITE_MOVIES);
                            mSharedPrefEditor.apply();
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            movieArrayList = getDataFromDB();
                            mAdapter = new MovieAdapter(movieArrayList, getActivity());
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            mRecyclerView.setAdapter(mAdapter);
                            Log.e(LOG_TAG, "BUTTON - FAVORITE SHOWN - OK INTERNET");
                            item.setChecked(true);
                            return true;
                        }
                    }
                } else {
                    movieArrayList.clear();
                    mSharedPrefEditor.putString(SORT_ORDER, FAVORITE_MOVIES);
                    mSharedPrefEditor.apply();
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    movieArrayList = getDataFromDB();
                    mAdapter = new MovieAdapter(movieArrayList, getActivity());
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.e(LOG_TAG, "BUTTON - FAVORITE SHOWN - NO INTERNET");
                    Snackbar.make(getActivity().findViewById(R.id.movie_root_layout),
                            "Your device is offline! Showing your favorite movies saved on your device.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    item.setChecked(true);
                    return true;

                }
        }
                return super.onOptionsItemSelected(item);
        }


    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        String mSort = mSharedPrefSettings.getString(SORT_ORDER, POPULAR_MOVIES);
        Log.e(LOG_TAG, "LOADER CREATED");
            return new MovieAsyncTaskLoader(getActivity(), mSort);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        movieArrayList = data;
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && !data.isEmpty()) {
            mAdapter = new MovieAdapter(data, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            Log.e(LOG_TAG, "LOAD FINISHED");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mRecyclerView.setAdapter(null);
        Log.e(LOG_TAG, "LOADER RESET");

    }

    // Helper method to verify if network connectivity is available
    public boolean deviceIsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
