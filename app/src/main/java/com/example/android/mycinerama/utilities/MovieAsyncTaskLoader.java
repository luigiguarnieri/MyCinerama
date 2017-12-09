package com.example.android.mycinerama.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.mycinerama.BuildConfig;
import com.example.android.mycinerama.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving movies' data from
 * themoviedb.org API.
 */

public class MovieAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {

    private static String SORT_BY;

    final private static String URL_SCHEME = "http";
    final private static String BASE_URL_AUTHORITY = "api.themoviedb.org";
    final private static String API_VERSION = "3";
    final private static String MOVIE_KEY = "movie";
    final private static String API_KEY = "api_key";

    /** Tag for the log messages */
    final private static String LOG_TAG = MovieAsyncTaskLoader.class.getSimpleName();

    public MovieAsyncTaskLoader(Context context, String sort) {
        super(context);
        MovieAsyncTaskLoader.SORT_BY = sort;
    }

    private static URL createUrl(){
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME)
                .encodedAuthority(BASE_URL_AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(MOVIE_KEY)
                .appendPath(SORT_BY)
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY);
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
        return url;
    }

    @Override
    public List<Movie> loadInBackground() {
        List<Movie> movieList= null;
        URL url = createUrl();
        String jsonResponse = null;
        try {
            jsonResponse = makehttpRequest(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            movieList = getMovieDataFromJson(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieList;
    }
    public static String makehttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            // If the code didn't successfully get the Movie data, there's no point in attemping
            // to parse it.
            return null;
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing inputStream", e);
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Movie> getMovieDataFromJson(String movieJsonString) throws JSONException {

        //Base URL Constant
        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185/";
        final String BACKDROPS_IMAGE_URL = "https://image.tmdb.org/t/p/w500/";

        //String variable to fetch movie data from JSON
        final String MOVIE_RESULT = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_BACKDROPS = "backdrop_path";

        //Json Object
        JSONObject movieJson = new JSONObject(movieJsonString);

        //Json Array
        JSONArray movieArray = movieJson.optJSONArray(MOVIE_RESULT);

        //Array List Creation
        ArrayList<Movie> movieArrayList = new ArrayList<>();

        //Iterate through each movie list
        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieObject = movieArray.getJSONObject(i);

            int id = movieObject.getInt(MOVIE_ID);
            String title = movieObject.getString(MOVIE_TITLE);
            String release_date = movieObject.getString(MOVIE_RELEASE_DATE);
            String poster_path = movieObject.getString(MOVIE_POSTER_PATH);
            String backdrops = movieObject.getString(MOVIE_BACKDROPS);
            double vote_average = movieObject.getDouble(MOVIE_VOTE_AVERAGE);
            String overview = movieObject.getString(MOVIE_OVERVIEW);

            //creating movie object to hold the data
            Movie movie = new Movie(id, title, release_date,BASE_IMAGE_URL + poster_path,
                    BACKDROPS_IMAGE_URL + backdrops, vote_average, overview);

            //adding data to ArrayList
            movieArrayList.add(movie);
        }
        return movieArrayList;
    }
}
