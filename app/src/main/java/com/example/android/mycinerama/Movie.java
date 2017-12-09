package com.example.android.mycinerama;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Movie} represents a movie.
 * It contains all the relevant data about a movie retrieved from the JSON: title,
 * release date, poster image, rating, and the plot synopsis.
 * It implements Parcelable to parsing retrieved data for every book
 * in the GridView from MovieActivity to MovieDetailActivity.
 */

public class Movie implements Parcelable{

    /**
     * ID of the movie
     */
    private final int mMovieID;

    /**
     * Title of the movie
     */
    private final String mMovieTitle;

    /**
     * Release date of the movie
     */
    private final String mMovieDate;

    /**
     * Url related to the poster of the movie
     */
    private final String mMoviePoster;

    /**
     * Url related to the backdrop image of the movie
     */
    private final String mMovieBackdrop;

    /**
     * Rating of the movie
     */
    private final double mMovieRating;

    /**
     * Plot synopsis of the movie
     */
    private final String mMoviePlot;

    public Movie(int movieID, String movieTitle, String movieDate, String moviePoster, String movieBackdrop,
                 double movieRating, String moviePlot) {
        mMovieID = movieID;
        mMovieTitle = movieTitle;
        mMovieDate = movieDate;
        mMoviePoster = moviePoster;
        mMovieBackdrop = movieBackdrop;
        mMovieRating = movieRating;
        mMoviePlot = moviePlot;
    }

    protected Movie(Parcel in) {
        mMovieID = in.readInt();
        mMovieTitle = in.readString();
        mMovieDate = in.readString();
        mMoviePoster = in.readString();
        mMovieBackdrop = in.readString();
        mMovieRating = in.readDouble();
        mMoviePlot = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * Get the ID of the movie.
     */
    public int getmMovieID() {
        return mMovieID;
    }

    /**
     * Get the title of the movie.
     */
    public String getmMovieTitle() {
        return mMovieTitle;
    }

    /**
     * Get the release date of the movie.
     */
    public String getmMovieDate() {
        return mMovieDate;
    }

    /**
     * Get the url for the backdrop image of the movie.
     */
    public String getmMovieBackdrop() {
        return mMovieBackdrop;
    }

    /**
     * Get the url for the poster of the movie.
     */
    public String getmMoviePoster() {
        return mMoviePoster;
    }

    /**
     * Get the rating of the movie.
     */
    public double getmMovieRating() {
        return mMovieRating;
    }

    /**
     * Get the plot synopsis of the movie.
     */
    public String getmMoviePlot() {
        return mMoviePlot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieID);
        dest.writeString(mMovieTitle);
        dest.writeString(mMovieDate);
        dest.writeString(mMoviePoster);
        dest.writeString(mMovieBackdrop);
        dest.writeDouble(mMovieRating);
        dest.writeString(mMoviePlot);
    }
}
