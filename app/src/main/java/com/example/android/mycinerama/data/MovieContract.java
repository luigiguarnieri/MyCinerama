package com.example.android.mycinerama.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database. This class is not necessary, but keeps
 * the code organized.
 */

public class MovieContract {
    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.mycinerama";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Cinerama.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Cinerama
     * can handle. For instance,
     *
     *     content://com.example.android.mycinerama/movie/
     *     [           BASE_CONTENT_URI         ][ PATH_MOVIE ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.mycinerama/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_MOVIE = "movie";

    /* Inner class that defines the table contents of the movies' table */
    public static final class MovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Movies' table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        /* Used internally as the name of our movies' table. */
        public static final String TABLE_NAME = "movie";

        /* Movie ID as returned by API, used to identify the movie if
        marked as favorite (stored as int in the database)*/
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Movie title as returned by API, used to identify the icon to be used */
        public static final String COLUMN_TITLE = "title";

        /* Movie release date as returned by API, stored as a String */
        public static final String COLUMN_DATE = "date";

        /* Movie poster image returned by API as a relative path (stored as String in the database) */
        public static final String COLUMN_POSTER = "poster";

        /* Movie backdrop image returned by API as a relative path (stored as String in the database) */
        public static final String COLUMN_BACKDROP = "backdrop";

        /* Movie rating as returned by API, stored as a float */
        public static final String COLUMN_RATING = "rating";

        /* Movie plot as returned by API, stored as a String */
        public static final String COLUMN_PLOT = "plot";
    }
}
