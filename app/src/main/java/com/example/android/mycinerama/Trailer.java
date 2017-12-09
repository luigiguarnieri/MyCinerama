package com.example.android.mycinerama;

/**
 * Created by luigiguarnieri on 29/11/17.
 */

public class Trailer {

    private final String mTrailerKey;

    private final String mTrailerName;

    private final String mTrailerImage;

    public Trailer(String trailerKey, String trailerName, String trailerImage) {
        mTrailerKey= trailerKey;
        mTrailerName = trailerName;
        mTrailerImage = trailerImage;
    }

    public String getmTrailerKey(){
        return mTrailerKey;
    }

    public String getmTrailerName(){
        return mTrailerName;
    }

    public String getmTrailerImage() {
        return mTrailerImage;
    }
}
