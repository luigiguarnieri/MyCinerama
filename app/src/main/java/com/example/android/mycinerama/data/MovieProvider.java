package com.example.android.mycinerama.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.mycinerama.data.MovieContract.MovieEntry.CONTENT_URI;
import static com.example.android.mycinerama.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * This class serves as the ContentProvider for all of Cinerama's data. This class allows us to
 * insert data, query data, and delete data.
 */
public class MovieProvider extends ContentProvider {

    /*
    * These constant will be used to match URIs with the data they are looking for. We will take
    * advantage of the UriMatcher class to make that matching MUCH easier than doing something
    * ourselves, such as using regular expressions.
    */
    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;

    /*
    * The URI Matcher used by this content provider. The leading "s" in this variable name
    * signifies that this UriMatcher is a static member variable of WeatherProvider and is a
    * common convention in Android programming.
    */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_MOVIE and
     * CODE_MOVIE_WITH_ID constants defined above.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_MOVIE and
     * CODE_MOVIE_WITH_ID
     */
    private static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Cinerama, we use CODE_MOVIE and CODE_MOVIE_WITH_ID.
         */

        /* This URI is content://com.example.android.mycinerama/movie/ */
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);

        /*
         * This URI would look something like content://com.example.android.mycinerama/movie/#
         * The "/#" signifies to the UriMatcher that if PATH_MOVIE is followed by ANY number,
         * that it should return the CODE_MOVIE_WITH_ID code
         */
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since MovieDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }


    /**
     * Handles query requests from clients. We will use this method in Cinerama to query for all
     * of our movies' data as well as to query for a particular movie.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.android.mycinerama/movie/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the movies' data in our table.
             *
             * In this case, we want to return a cursor that contains every row of movies' data
             * in our movie table.
             */
            case CODE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.android.mycinerama/movie/#
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the movies'data for a particular movie.
             *
             * In this case, we want to return a cursor that contains one row of movies' data for
             * a particular movie.
             */
            case CODE_MOVIE_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Handles requests to insert a set of movies' data. In Cinerama we are only going to be
     * inserting multiple rows of data at a time from a weather forecast.
     *
     * @param uri        The content:// URI of the insertion request.
     * @param values     Sets of value pairs to add to the database.
     *                   This must not be {@code null}.
     *
     * @return returnUri The content:// URI of the inserted movies' data.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case CODE_MOVIE:
                rowsDeleted = db.delete(
                        TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TABLE_NAME + "'");
                break;
            case CODE_MOVIE_WITH_ID:
                rowsDeleted = db.delete(TABLE_NAME,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsDeleted;
    }

    /**
     * Updates data at a given URI with optional arguments for more fine tuned updates.
     *
     * @param uri           The full URI to query
     * @param values        A set of column_name/value pairs to add to the database.
     *                      This must not be null
     * @param selection     An optional restriction to apply to rows when deleting
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (match) {
            case CODE_MOVIE:
                rowsUpdated = db.update(TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_MOVIE_WITH_ID:
                rowsUpdated = db.update(TABLE_NAME, values,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * This method handles requests for the MIME type of the data at the given URI.
     *
     * @param  uri the URI to query.
     * @return MIME type string
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case CODE_MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
