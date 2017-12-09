package com.example.android.mycinerama;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luigiguarnieri on 29/11/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private List<Review> mReviewList;
    private Context mContext;

    ReviewAdapter(List<Review> reviewList, Context context){
        this.mReviewList = reviewList;
        this.mContext = context;

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
        Review review = mReviewList.get(position);

        holder.mReviewAuthor.setText(review.getmReviewAuthor());
        holder.mReviewContent.setText(review.getmReviewContent());

    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.review_content)
        TextView mReviewContent;
        @BindView(R.id.review_author)
        TextView mReviewAuthor;


        public ReviewViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
