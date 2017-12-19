package com.example.android.mycinerama;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.mycinerama.adapters.MovieAdapter;
import com.example.android.mycinerama.data.MovieContract;
import com.example.android.mycinerama.models.Movie;
import com.example.android.mycinerama.utilities.FavoriteAsyncTask;
import com.example.android.mycinerama.utilities.MovieAsyncTask;
import com.example.android.mycinerama.utilities.RecyclerItemClickListener;
import com.example.android.mycinerama.utilities.UtilityActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieActivityFragment extends Fragment
    implements MovieAsyncTask.AsyncTaskMovieResponse, FavoriteAsyncTask.AsyncTaskFavoriteResponse {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MovieActivityFragment.class.getName();

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    private ArrayList<Movie> movieArrayList = null;
    private MovieAdapter mAdapter;

    private static final String SORT_ORDER = "sort";
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String FAVORITE_MOVIES = "favorite";
    private static final String MOVIES_KEY = "movies";

    // Default sorting value to fetch movies with the TMDb API.
    private String mSortBy = POPULAR_MOVIES;

    /*
     * The columns of data that we are interested to store movies into the database
     * of favorite movies.
     */
    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_BACKDROP,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_PLOT
};

    private SharedPreferences mSharedPrefSettings;
    private SharedPreferences.Editor mSharedPrefEditor;



    public MovieActivityFragment() {
        // Required empty public constructor
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(Movie movie);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_activity_fragment, menu);

        MenuItem sortByPopular = menu.findItem(R.id.filter_popular);
        MenuItem sortByTopRated = menu.findItem(R.id.filter_top_rated);
        MenuItem sortByFavorite = menu.findItem(R.id.filter_favorite);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), POPULAR_MOVIES)) {
                if (!sortByPopular.isChecked()) {
                    sortByPopular.setChecked(true);
                }
            } else if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), TOP_RATED_MOVIES)) {
                if (!sortByTopRated.isChecked()) {
                    sortByTopRated.setChecked(true);
                }
            } else if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), FAVORITE_MOVIES)) {
                if (!sortByFavorite.isChecked()) {
                    sortByFavorite.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.filter_popular:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSharedPrefEditor.putString(SORT_ORDER, POPULAR_MOVIES);
                mSharedPrefEditor.apply();
                mSortBy = POPULAR_MOVIES;
                updateMovies(mSortBy);
                return true;
            case R.id.filter_top_rated:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSharedPrefEditor.putString(SORT_ORDER, TOP_RATED_MOVIES);
                mSharedPrefEditor.apply();
                mSortBy = TOP_RATED_MOVIES;
                updateMovies(mSortBy);
                return true;
            case R.id.filter_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSharedPrefEditor.putString(SORT_ORDER, FAVORITE_MOVIES);
                mSharedPrefEditor.apply();
                mSortBy = FAVORITE_MOVIES;
                updateMovies(mSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_activity, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new MovieAdapter(new ArrayList<Movie>(), getActivity());

        // Change number of Grid columns if device is in portrait or landscape mode
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Movie movie = movieArrayList.get(position);
                        ((Callback) getActivity()).onItemSelected(movie);
                    }
                }));

        mSharedPrefSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPrefEditor = mSharedPrefSettings.edit();
        mSharedPrefEditor.apply();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_ORDER)) {
                mSortBy = savedInstanceState.getString(SORT_ORDER);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                movieArrayList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mAdapter = new MovieAdapter(movieArrayList, getActivity());
                mRecyclerView.setAdapter(mAdapter);
            } else {
                updateMovies(mSortBy);
            }
        } else {
            updateMovies(mSortBy);
        }


        return rootView;
    }

    // Helper method to dynamically calculate the number of columns and
    // the layout would adapt to the screen size and orientation
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // Change this divider to adjust the size of the poster
        int widthDivider = 350;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    // Update movies in the RecyclerView if device is connected to internet.
    // If sorting value is different from "favorite", movies data are retrieved
    // with related AsyncTask. Else favorite movies are queried from movies.db
    private void updateMovies(String sortBy) {
        if (UtilityActivity.deviceIsConnected(getActivity())) {
                if (!(sortBy.contentEquals(FAVORITE_MOVIES))) {
                    new MovieAsyncTask(this).execute(sortBy);
                    Log.e(LOG_TAG, "updated online movies");
                } else {
                    new FavoriteAsyncTask(getActivity(), this).execute();
                    Log.e(LOG_TAG, "updated FAVORITE movies");
                }
        } else {
            // Toast an error message if device is not connected to internet.
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(),
                            R.string.toast_message_no_connection, Toast.LENGTH_LONG)
                            .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(mSharedPrefSettings.getString(SORT_ORDER, null), POPULAR_MOVIES)) {
                outState.putString(SORT_ORDER, mSortBy);
            }
        }
        if (movieArrayList != null) {
            outState.putParcelableArrayList(MOVIES_KEY, movieArrayList);
        }
        super.onSaveInstanceState(outState);
    }

    // Updated properly the MovieActivity if a movie has been (un)checked as
    // favorite in the meantime in the DetailActivity by the user.
    @Override
    public void onResume() {
        if (mSortBy.contentEquals(FAVORITE_MOVIES)) {
            new FavoriteAsyncTask(getActivity(), this).execute();
            Log.e(LOG_TAG, "updated FAVORITE movies");
        }
        super.onResume();
    }

    // Method to retrieve results from onPostExecute of FavoriteAsyncTask
    // and use them to populate list in the RecyclerView.
    @Override
    public void processFavoriteExecuted(List<Movie> movies) {
        if (movies != null) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (mAdapter != null) {
                mAdapter = new MovieAdapter(movies, getActivity());
                mRecyclerView.setAdapter(mAdapter);
            }
            movieArrayList = new ArrayList<>();
            movieArrayList.addAll(movies);
        }

    }

    // Method to retrieve results from onPostExecute of MovieAsyncTask
    // and use them to populate list in the RecyclerView.
    @Override
    public void processMovieExecuted(List<Movie> movies) {
        if (movies != null) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (mAdapter != null) {
                mAdapter = new MovieAdapter(movies, getActivity());
                mRecyclerView.setAdapter(mAdapter);
            }
            movieArrayList = new ArrayList<>();
            movieArrayList.addAll(movies);
        }

    }
}
