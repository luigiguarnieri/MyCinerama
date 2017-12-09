package com.example.android.mycinerama;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link MovieAdapter} is an {@link RecyclerView.Adapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Movie} objects.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mMovieList;
    private Context mContext;

    MovieAdapter(List<Movie> movieList, Context context) {
        this.mMovieList = movieList;
        this.mContext = context;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //layout inflater
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.movie_grid_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, final int position) {

        // Get the {@link Movie} object located at this position in the list
        Movie currentMovie = mMovieList.get(position);

        // Display the title of the movie in that TextView
        movieViewHolder.movieTitleView.setText(currentMovie.getmMovieTitle());

        // Display the release year of the movie in that TextView
        try {
            movieViewHolder.movieReleaseDateView.setText(formatReleaseDate(currentMovie.getmMovieDate()));
        } catch (ParseException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            e.printStackTrace();
            Log.e(LOG_TAG, String.valueOf(R.string.error_formatting_movie_release_date), e.getCause());
        }

        // Check if a retrieved an image themoviedb.org API and it's no empty
        if (currentMovie.getmMoviePoster() != null && !currentMovie.getmMoviePoster().isEmpty()) {
            // Use the Picasso library to download movie poster and set it in the ImageView
            Picasso.with(mContext)
                    .load(currentMovie.getmMoviePoster())
                    .into(movieViewHolder.moviePosterImage);
        } else {
            // If no image is retrieved, it's set a dummy movie poster in the ImageView
            movieViewHolder.moviePosterImage.setImageResource(R.drawable.image_view_empty_grid);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    /**
     * ViewHolder class
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.iv_movie_poster) ImageView moviePosterImage;
        @BindView(R.id.tv_movie_title) TextView movieTitleView;
        @BindView(R.id.tv_movie_release_year) TextView movieReleaseDateView;
        @BindView(R.id.background_movie_group)
        RelativeLayout movieBackgroundGroup;



        MovieViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Format the date retrieved with JSON parsing from ISO 8601 format to a
     * local format showing only the year (i.e. "2017").
     */
    static String formatReleaseDate(String movieReleaseDate) throws ParseException {
        SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return finalFormat.format(startFormat.parse(movieReleaseDate));
    }
}

