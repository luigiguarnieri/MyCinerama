package com.example.android.mycinerama.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * {@link UtilityActivity} is an Class with helper methods inside that are used
 * in other Activities to format movies data or check device's internet connection.
 */
public class UtilityActivity extends AppCompatActivity {

    /**
     * Helper method to check if device is connected to internet.
     */
    public static boolean deviceIsConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Format the date retrieved with JSON parsing from ISO 8601 format to a
     * local format showing only the year (i.e. "2017").
     */
    public static String formatReleaseDate(String movieReleaseDate) throws ParseException {
        SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return finalFormat.format(startFormat.parse(movieReleaseDate));
    }
}
