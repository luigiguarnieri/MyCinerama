package com.example.android.mycinerama.utilities;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.mycinerama.BuildConfig;
import com.example.android.mycinerama.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewAsyncTaskLoader extends AsyncTaskLoader<List<Review>> {

    private static int MOVIE_ID;

    final private static String URL_SCHEME = "https";
    final private static String BASE_URL_AUTHORITY = "api.themoviedb.org";
    final private static String API_VERSION = "3";
    final private static String MOVIE_KEY = "movie";
    final private static String API_KEY = "api_key";
    final private static String APPEND_QUERY = "append_to_response";
    final private static String REVIEW_KEY = "reviews";

    /** Tag for the log messages */
    final private static String LOG_TAG = ReviewAsyncTaskLoader.class.getSimpleName();

    public ReviewAsyncTaskLoader(Context context, int movieId) {
        super(context);
        ReviewAsyncTaskLoader.MOVIE_ID = movieId;
    }

    //EXAMPLE: https://api.themoviedb.org/3/movie/263115?api_key=###&append_to_response=reviews
    private static URL createUrl(){
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME)
                .encodedAuthority(BASE_URL_AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(MOVIE_KEY)
                .appendPath(String.valueOf(MOVIE_ID))
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .appendQueryParameter(APPEND_QUERY, REVIEW_KEY);
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
    public List<Review> loadInBackground() {
        List<Review> reviewList= null;
        URL url = createUrl();
        String jsonResponse = null;
        try {
            jsonResponse = MovieAsyncTaskLoader.makehttpRequest(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            reviewList = getReviewFromJson(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    private static List<Review> getReviewFromJson(String reviewJsonString) throws JSONException {

        //String variable to fetch review data from JSON
        final String MOVIE_REVIEW = "reviews";
        final String MOVIE_REVIEW_RESULT = "results";
        final String MOVIE_REVIEW_AUTHOR = "author";
        final String MOVIE_REVIEW_CONTENT = "content";

        //Json Object
        JSONObject reviewJson = new JSONObject(reviewJsonString);

        //Json Object
        JSONObject reviewResult = reviewJson.optJSONObject(MOVIE_REVIEW);

        //Json Array
        JSONArray resultArray = reviewResult.optJSONArray(MOVIE_REVIEW_RESULT);

        //Array List Creation
        ArrayList<Review> reviewArrayList = new ArrayList<>();

        //Iterate through each review list
        for (int i = 0; i < resultArray.length(); i++) {

            JSONObject reviewObject = resultArray.getJSONObject(i);

            String reviewAuthor = reviewObject.getString(MOVIE_REVIEW_AUTHOR);
            String reviewContent = reviewObject.getString(MOVIE_REVIEW_CONTENT);

            //creating movie object to hold the data
            Review review = new Review(reviewAuthor, reviewContent);

            //adding data to ArrayList
            reviewArrayList.add(review);
        }
        return reviewArrayList;
    }
}
