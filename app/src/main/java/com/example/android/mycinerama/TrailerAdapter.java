package com.example.android.mycinerama;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luigiguarnieri on 03/12/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mTrailerList;
    private Context mContext;

    TrailerAdapter(List<Trailer> trailerList, Context context){
        this.mTrailerList = trailerList;
        this.mContext = context;

    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //layout inflater
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.movie_trailer_item, parent, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);

        String trailerImage = trailer.getmTrailerImage();

        holder.mTrailerName.setText(trailer.getmTrailerName());

        // Check if a retrieved an image themoviedb.org API and it's no empty
        if (trailerImage != null && !trailerImage.isEmpty()) {
            // Use the Picasso library to download movie poster and set it in the ImageView
            Picasso.with(mContext)
                    .load(trailerImage)
                    .into(holder.mTrailerImage);
        } else {
            // If no image is retrieved, it's set a dummy movie poster in the ImageView
            holder.mTrailerImage.setImageResource(R.drawable.example_movie_backdrop);
        }

    }

    @Override
    public int getItemCount() {
        if (null == mTrailerList) return 0;
        return mTrailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.trailer_image)
        ImageView mTrailerImage;
        @BindView(R.id.trailer_name)
        TextView mTrailerName;


        public TrailerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
