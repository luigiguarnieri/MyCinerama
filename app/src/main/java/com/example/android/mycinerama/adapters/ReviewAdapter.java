package com.example.android.mycinerama.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mycinerama.R;
import com.example.android.mycinerama.models.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link ReviewAdapter} is an {@link RecyclerView.Adapter} that can provide the layout for movie's
 * reviews, which is a list of {@link Review} objects.
 */

@SuppressWarnings("ALL")
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {


    private List<Review> mReviewList;

    public ReviewAdapter(List<Review> reviewList){
        this.mReviewList = reviewList;

    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //layout inflater
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.movie_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {

        // Get the {@link Review} object located at this position in the list
        Review review = mReviewList.get(position);

        // Display the author of the review in that TextView
        holder.mReviewAuthor.setText(review.getmReviewAuthor());

        // Display the content of the review in that TextView
        holder.mReviewContent.setText(review.getmReviewContent());

    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }

    /**
     * ViewHolder class
     */
    @SuppressWarnings({"CanBeFinal", "unused"})
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        @SuppressWarnings("unused")
        View mView;
        @BindView(R.id.review_content)
        TextView mReviewContent;
        @BindView(R.id.review_author)
        TextView mReviewAuthor;


        ReviewViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
