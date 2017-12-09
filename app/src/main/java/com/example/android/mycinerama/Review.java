package com.example.android.mycinerama;

/**
 * Created by luigiguarnieri on 29/11/17.
 */

public class Review {

    private final String mReviewAuthor;

    private final String mReviewContent;

    public Review(String reviewAuthor, String reviewContent) {
        mReviewAuthor = reviewAuthor;
        mReviewContent = reviewContent;
    }

    public String getmReviewAuthor(){
        return mReviewAuthor;
    }

    public String getmReviewContent(){
        return mReviewContent;
    }
}
