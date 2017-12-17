package com.example.android.mycinerama.models;

/**
 * {@link Trailer} represents a movie's trailer.
 * It contains all the relevant data about a movie's trailer retrieved from the JSON:
 * trailer's movie key for Youtube, trailer's title and image.
 */

public class Trailer {

    /**
     * Trailer's movie key
     */
    private final String mTrailerKey;

    /**
     * Title of the trailer
     */
    private final String mTrailerName;

    /**
     * Image url of the trailer
     */
    private final String mTrailerImage;

    public Trailer(String trailerKey, String trailerName, String trailerImage) {
        mTrailerKey= trailerKey;
        mTrailerName = trailerName;
        mTrailerImage = trailerImage;
    }

    /**
     * Get trailer's movie key
     */
    public String getmTrailerKey(){
        return mTrailerKey;
    }

    /**
     * Get title of the trailer
     */
    public String getmTrailerName(){
        return mTrailerName;
    }

    /**
     * Get image url of the trailer
     */
    public String getmTrailerImage() {
        return mTrailerImage;
    }
}
