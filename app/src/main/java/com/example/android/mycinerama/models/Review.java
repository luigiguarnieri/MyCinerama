package com.example.android.mycinerama.models;

import java.io.Serializable;

/**
 * {@link Review} represents a movie's review.
 * It contains all the relevant data about a movie's reviews retrieved from the JSON:
 * author and content of a review.
 */

public class Review implements Serializable {

    /**
     * Author of the review
     */
    private final String mReviewAuthor;

    /**
     * Content of the review
     */
    private final String mReviewContent;

    public Review(String reviewAuthor, String reviewContent) {
        mReviewAuthor = reviewAuthor;
        mReviewContent = reviewContent;
    }

    /**
     * Get the author of the review.
     */
    public String getmReviewAuthor(){
        return mReviewAuthor;
    }

    /**
     * Get the content of the review.
     */
    public String getmReviewContent(){
        return mReviewContent;
    }
}
