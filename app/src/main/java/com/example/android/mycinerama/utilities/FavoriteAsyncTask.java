package com.example.android.mycinerama.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.mycinerama.models.Movie;
import com.example.android.mycinerama.MovieActivityFragment;
import com.example.android.mycinerama.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving movies' data from
 * local movies.db, for user has stored them as favorites.
 */

public class FavoriteAsyncTask extends AsyncTask<Void, Void, List<Movie>> {

    /** Interface to get the result (List<Movie> movies) of the AsyncTask and use it
     * calling its onPostExecute in another activity (MovieActivityFragment)
     * through a delegate. */
    public interface AsyncTaskFavoriteResponse {
        void processFavoriteExecuted(List<Movie> movies);
    }

    private AsyncTaskFavoriteResponse delegate = null;

    @SuppressWarnings("CanBeFinal")
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public FavoriteAsyncTask(Context context, AsyncTaskFavoriteResponse delegate) {
        mContext = context;
        this.delegate = delegate;
    }

    private List<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
        List<Movie> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                    int movie_id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                    String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE));
                    String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
                    String backdrop = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));
                    double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
                    String plot = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT));
                Movie movie = new Movie(movie_id, title, date, poster, backdrop, movie_id, rating, plot);
                results.add(movie);
                Log.e("FAVORITE TASK", "cursor retrieved and attached to moviearraylist");
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieActivityFragment.MOVIE_COLUMNS,
                null,
                null,
                null
        );
        return getFavoriteMoviesDataFromCursor(cursor);
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        delegate.processFavoriteExecuted(movies);
        }
    }
