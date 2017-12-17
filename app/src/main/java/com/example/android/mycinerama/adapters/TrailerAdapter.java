package com.example.android.mycinerama.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mycinerama.R;
import com.example.android.mycinerama.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link TrailerAdapter} is an {@link RecyclerView.Adapter} that can provide the layout for movie's
 * trailers, which is a list of {@link Trailer} objects.
 */

@SuppressWarnings("ALL")
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    private List<Trailer> mTrailerList;
    private Context mContext;

    public TrailerAdapter(List<Trailer> trailerList, Context context){
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

        // Get the {@link Trailer} object located at this position in the list
        Trailer trailer = mTrailerList.get(position);

        // Display the author of the review in that TextView
        holder.mTrailerName.setText(trailer.getmTrailerName());

        // Get the trailer image for the movie's trailer
        String trailerImage = trailer.getmTrailerImage();
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

    /**
     * ViewHolder class
     */
    @SuppressWarnings({"CanBeFinal", "unused"})
    class TrailerViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.trailer_image)
        ImageView mTrailerImage;
        @BindView(R.id.trailer_name)
        TextView mTrailerName;


        TrailerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
