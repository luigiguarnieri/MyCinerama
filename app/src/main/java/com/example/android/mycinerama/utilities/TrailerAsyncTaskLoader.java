package com.example.android.mycinerama.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.mycinerama.BuildConfig;
import com.example.android.mycinerama.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luigiguarnieri on 03/12/17.
 */

public class TrailerAsyncTaskLoader extends AsyncTaskLoader<List<Trailer>> {

    private static int MOVIE_ID;

    final private static String URL_SCHEME = "https";
    final private static String BASE_URL_AUTHORITY = "api.themoviedb.org";
    final private static String API_VERSION = "3";
    final private static String MOVIE_KEY = "movie";
    final private static String API_KEY = "api_key";
    final private static String APPEND_QUERY = "append_to_response";
    final private static String TRAILER_KEY = "videos";

    /**
     * Tag for the log messages
     */
    final private static String LOG_TAG = TrailerAsyncTaskLoader.class.getSimpleName();

    public TrailerAsyncTaskLoader(Context context, int movieId) {
        super(context);
        TrailerAsyncTaskLoader.MOVIE_ID = movieId;
    }

    //EXAMPLE: https://api.themoviedb.org/3/movie/263115?api_key=###&append_to_response=videos
    private static URL createUrl() {
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME)
                .encodedAuthority(BASE_URL_AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(MOVIE_KEY)
                .appendPath(String.valueOf(MOVIE_ID))
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .appendQueryParameter(APPEND_QUERY, TRAILER_KEY);
        String movieUrl = builder.build().toString();
        try {
            url = new URL(movieUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem building the URL ", e.getCause());
        }
        Log.e(LOG_TAG, "Review to retrieve reviews is " + movieUrl);
        return url;
    }

    @Override
    public List<Trailer> loadInBackground() {
        List<Trailer> trailerList = null;
        URL url = createUrl();
        String jsonResponse = null;
        try {
            jsonResponse = MovieAsyncTaskLoader.makehttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            trailerList = getTrailerFromJson(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerList;
    }

    private static List<Trailer> getTrailerFromJson(String trailerJsonString) throws JSONException {

        final String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/";
        final String FIRST_IMAGE_YOUTUBE_TRAILER = "/0.jpg";

        //String variable to fetch trailer data from JSON
        final String MOVIE_TRAILER = "videos";
        final String MOVIE_TRAILER_RESULT = "results";
        final String MOVIE_TRAILER_KEY = "key";
        final String MOVIE_TRAILER_NAME = "name";

        //Json Object for movie details (reviews and trailers)
        JSONObject trailerJson = new JSONObject(trailerJsonString);

        JSONObject trailerResult = trailerJson.optJSONObject(MOVIE_TRAILER);

        JSONArray resultTrailerArray = trailerResult.optJSONArray(MOVIE_TRAILER_RESULT);

        //Array List Creation
        ArrayList<Trailer> trailerArrayList = new ArrayList<>();

        for (int i = 0; i < resultTrailerArray.length(); i++) {

            JSONObject trailerObject = resultTrailerArray.getJSONObject(i);

            String trailerKey = trailerObject.getString(MOVIE_TRAILER_KEY);
            String trailerName = trailerObject.getString(MOVIE_TRAILER_NAME);


            //creating movie object to hold the data
            Trailer trailer = new Trailer(trailerKey, trailerName, YOUTUBE_IMAGE_URL + trailerKey + FIRST_IMAGE_YOUTUBE_TRAILER);

            //adding data to ArrayList
            trailerArrayList.add(trailer);
        }
        return trailerArrayList;
    }

}
