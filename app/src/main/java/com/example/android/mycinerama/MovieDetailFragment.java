package com.example.android.mycinerama;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mycinerama.adapters.ReviewAdapter;
import com.example.android.mycinerama.adapters.TrailerAdapter;
import com.example.android.mycinerama.data.MovieContract;
import com.example.android.mycinerama.models.Movie;
import com.example.android.mycinerama.models.Review;
import com.example.android.mycinerama.models.Trailer;
import com.example.android.mycinerama.utilities.RecyclerItemClickListener;
import com.example.android.mycinerama.utilities.ReviewAsyncTaskLoader;
import com.example.android.mycinerama.utilities.TrailerAsyncTaskLoader;
import com.example.android.mycinerama.utilities.UtilityActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass for MovieDetailActivity.
 */
@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "WeakerAccess"})
public class MovieDetailFragment extends Fragment {

    /**
     * Tag to identify the MovieDetailFragment
     */
    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MovieDetailFragment.class.getName();

    static final String MOVIE_DETAILS = "movie";

    static final String REVIEW_RECYCLER_VIEW_VISIBILITY = "review";

    static final String TRAILER_RECYCLER_VIEW_VISIBILITY = "trailer";

    static final String REVIEW_CONTAINER_VISIBILITY = "review_visibile";

    static final String TRAILER_CONTAINER_VISIBILITY = "trailer_visible";

    static final String REVIEW_LIST = "review_list";

    static final String TRAILER_LIST = "trailer_list";

    static final String REVIEW_LIST_STATE = "review_list_state";

    static final String TRAILER_LIST_STATE = "trailer_list_state";

    LinearLayoutManager mReviewLayoutManager;
    LinearLayoutManager mTrailerLayoutManager;

    Parcelable stateReview;

    Parcelable stateTrailer;

    private Movie movie;

    private int movieId;

    private final String YOUTUBE_APP_URL = "vnd.youtube:";
    private final String YOUTUBE_WEB_URL = "http://www.youtube.com/watch?v=";

    private String thumbnail;
    private String title;
    private String overview;
    private String backdrop;
    private String release;
    private int voteCount;
    private double voteRating;

    private List<Review> reviewArrayList;
    private ReviewAdapter mReviewAdapter;
    private List<Trailer> trailerArrayList;
    private TrailerAdapter mTrailerAdapter;
    private ShareActionProvider mShareActionProvider;

    @BindView(R.id.review_recycler_view)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.favorite_button)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_detail_movie_title)
    TextView movieTitleTextView;
    @BindView(R.id.tv_detail_movie_rating)
    TextView movieRatingTextView;
    @BindView(R.id.synopsis_cardview_plot)
    TextView moviePlotTextView;
    @BindView(R.id.tv_detail_movie_release_year)
    TextView movieReleaseYearTextView;
    @BindView(R.id.iv_detail_movie_backdrop)
    ImageView movieBackdropImageView;
    @BindView(R.id.iv_detail_movie_poster)
    ImageView moviePosterImageView;
    @BindView(R.id.tv_detail_movie_vote_count)
    TextView movieVoteCountTextView;
    @BindView(R.id.review_movie_container)
    CardView reviewContainerCardView;
    @BindView(R.id.review_section_title)
    TextView reviewSectionTitle;
    @BindView(R.id.trailer_section_title)
    TextView trailerSectionTitle;
    @BindView(R.id.trailer_movie_container)
    CardView trailerContainerCardView;
    @BindView(R.id.detail_root_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.detail_nested_scrollview)
    NestedScrollView detailFragmentContainer;

    private static final int REVIEW_LOADER_ID = 33;
    private static final int TRAILER_LOADER_ID = 44;


    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu resource file if movie exists.
        if (movie != null) {
            inflater.inflate(R.menu.menu_detail_activity_fragment, menu);
            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);
            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        }
    }

    // Method to create an intent to share movie's title (and movie's trailer if available).
    private Intent createShareIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving all the UI elemente about Reviews and Trailers to use
        // them if user changes configuration (device rotation).
        outState.putInt(REVIEW_RECYCLER_VIEW_VISIBILITY, mReviewRecyclerView.getVisibility());
        Log.e(LOG_TAG, "Put " + REVIEW_RECYCLER_VIEW_VISIBILITY);
        outState.putInt(TRAILER_RECYCLER_VIEW_VISIBILITY, mTrailerRecyclerView.getVisibility());
        Log.e(LOG_TAG, "Put " + TRAILER_RECYCLER_VIEW_VISIBILITY);
        outState.putInt(REVIEW_CONTAINER_VISIBILITY, reviewContainerCardView.getVisibility());
        outState.putInt(TRAILER_CONTAINER_VISIBILITY, trailerContainerCardView.getVisibility());
        outState.putParcelable(REVIEW_LIST_STATE, mReviewRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelable(TRAILER_LIST_STATE, mTrailerRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putSerializable(REVIEW_LIST, (Serializable) reviewArrayList);
        outState.putSerializable(TRAILER_LIST, (Serializable) trailerArrayList);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Verify if review container was visible to set the visibility of the recyclerview inside it
            int reviewContainerVisibility = savedInstanceState.getInt(REVIEW_CONTAINER_VISIBILITY);
            if (reviewContainerVisibility == View.VISIBLE) {
                int reviewRecyclerViewVisibility = savedInstanceState.getInt(REVIEW_RECYCLER_VIEW_VISIBILITY, 0);
                mReviewRecyclerView.setVisibility(reviewRecyclerViewVisibility);
                Log.e(LOG_TAG, "Got " + REVIEW_RECYCLER_VIEW_VISIBILITY);
            } else {
                // If review container was invisible, its state remains the same.
                reviewContainerCardView.setVisibility(View.INVISIBLE);
            }
            // Verify if trailer container was visible to set the visibility of the recyclerview inside it
            int trailerContainerVisibility = savedInstanceState.getInt(TRAILER_CONTAINER_VISIBILITY);
            if (trailerContainerVisibility == View.VISIBLE) {
                int trailerRecyclerViewVisibility = savedInstanceState.getInt(TRAILER_RECYCLER_VIEW_VISIBILITY, 0);
                mTrailerRecyclerView.setVisibility(trailerRecyclerViewVisibility);
                Log.e(LOG_TAG, "Got " + TRAILER_RECYCLER_VIEW_VISIBILITY);
            } else {
                // If trailer container was invisible, its state remains the same.
                trailerContainerCardView.setVisibility(View.INVISIBLE);
            }
            stateReview = savedInstanceState.getParcelable(REVIEW_LIST_STATE);
            stateTrailer = savedInstanceState.getParcelable(TRAILER_LIST_STATE);
            mReviewRecyclerView.getLayoutManager().onRestoreInstanceState(stateReview);
            Log.e(LOG_TAG, "Got " + REVIEW_LIST_STATE);
            mTrailerRecyclerView.getLayoutManager().onRestoreInstanceState(stateTrailer);
            Log.e(LOG_TAG, "Got " + TRAILER_LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Retrieve "movie" object sent via Intent to MovieDetailActivity
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(MovieDetailFragment.MOVIE_DETAILS);
        } else if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(MovieDetailFragment.MOVIE_DETAILS);
        }

        if (movie != null) {
            detailFragmentContainer.setVisibility(View.VISIBLE);
        } else {
            detailFragmentContainer.setVisibility(View.INVISIBLE);
        }

        // If no bundle is present, create a new Arraylist for Review(s) elements.
        if (savedInstanceState == null) {
            reviewArrayList = new ArrayList<>();
        } else {
                // If a bundle is present, get saved reviewArrayList to show its data
                // through the adapter.
                reviewArrayList = (List<Review>) savedInstanceState.getSerializable(REVIEW_LIST);
            if (reviewArrayList == null && reviewArrayList.isEmpty()) {
                // If there is not a list of reviews to populate mReviewRecyclerView,
                // reviews' container is made invisible.
                reviewContainerCardView.setVisibility(View.INVISIBLE);
            }

        }
        mReviewAdapter = new ReviewAdapter(reviewArrayList);
        mReviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        // If no bundle is present, create a new Arraylist for Trailer(s) elements.
        if (savedInstanceState == null) {
            trailerArrayList = new ArrayList<>();
        } else {
            // If a bundle is present, get saved trailerArrayList to show its data
            // through the adapter.
            trailerArrayList = (List<Trailer>) savedInstanceState.getSerializable(TRAILER_LIST);
            if (trailerArrayList == null && trailerArrayList.isEmpty()) {
                // If there is not a list of trailers to populate mTrailerRecyclerView,
                // trailers' container is made invisible.
                trailerContainerCardView.setVisibility(View.INVISIBLE);
            }
        }
        mTrailerAdapter = new TrailerAdapter(trailerArrayList, getActivity());
        mTrailerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(mTrailerLayoutManager);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        if (movie != null) {
            movieId = movie.getmMovieID();

            Log.e(LOG_TAG, "MovieID is " + movieId);

            Log.e(LOG_TAG, movie.getmMovieTitle() + movie.getmMovieRating() +
                    movie.getmMoviePoster() + movie.getmMovieBackdrop() + movie.getmMovieID() +
                    movie.getmMoviePlot() + movie.getmMovieDate());

            // Retrieve the movie's title and set it on the TextView.
            title = movie.getmMovieTitle();
            movieTitleTextView.setText(title);

            // Retrieve the movie's rating and set it on the TextView.
            voteRating = movie.getmMovieRating();
            movieRatingTextView.setText(String.valueOf(voteRating));

            // Retrieve the movie's plot synopsis and set it on the TextView.
            overview = movie.getmMoviePlot();
            moviePlotTextView.setText(overview);

            // Retrieve the movie's total voteRating count and set it on the TextView.
            voteCount = movie.getmMovieVoteCount();
            movieVoteCountTextView.setText(getString(R.string.movie_detail_vote_count_text, voteCount));

            // Retrieve the movie's release date and set it on the TextView.
            release = movie.getmMovieDate();
            try {
                movieReleaseYearTextView.setText(UtilityActivity.formatReleaseDate(release));
            } catch (ParseException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                e.printStackTrace();
                Log.e(LOG_TAG, getString(R.string.error_formatting_movie_release_date), e.getCause());
            }

            // Retrieve the movie's backdrop and set it on the ImageView using Picasso Library.
            backdrop = movie.getmMovieBackdrop();
            // Check if a retrieved an image from themoviedb.org API and it's no empty
            if (backdrop != null && !backdrop.isEmpty()) {
                // Use the Picasso library to download movie poster and set it in the ImageView
                Picasso.with(getActivity().getApplicationContext()).load((backdrop)).into(movieBackdropImageView);
            } else {
                // If no image is retrieved, it's set a dummy movie poster in the ImageView
                movieBackdropImageView.setImageResource(R.drawable.image_view_empty_grid);
            }

            // Retrieve the movie's thumbnail image and set it on the ImageView using Picasso Library.
            thumbnail = movie.getmMoviePoster();
            if (thumbnail != null && !thumbnail.isEmpty()) {
                // Use the Picasso library to download movie poster and set it in the ImageView
                Picasso.with(getActivity().getApplicationContext()).load((thumbnail)).into(moviePosterImageView);
            } else {
                // If no image is retrieved, it's set a dummy movie poster in the ImageView
                movieBackdropImageView.setImageResource(R.drawable.image_view_empty_grid);
            }

            // Check if a movie is favorite or not, and set a different image for the FAB
            // to show this to the user.
            if (checkFavoriteMovie(movieId))
                floatingActionButton.setImageResource(R.drawable.ic_star_full);
            else
                floatingActionButton.setImageResource(R.drawable.ic_star_empty);

            // Set OnItemTouchListener on mTrailerRecyclerView to let the user click on the image of the movie
            // trailer and open it on YouTube app or on internet browser (if app is not installed).
            mTrailerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(),
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Trailer trailer = trailerArrayList.get(position);
                            youTubeTrailerVideo(getActivity(), trailer.getmTrailerKey());
                        }
                    }));

            // Set OnClickListener on reviewSectionTitle to let the user click on the section
            // to expand it and read movie reviews.
            reviewSectionTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReviewRecyclerView.getVisibility() == View.GONE) {
                        mReviewRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mReviewRecyclerView.setVisibility(View.GONE);
                    }
                }
            });

            // Set OnClickListener on trailerSectionTitle to let the user click on the section
            // to expand it and see movie trailers.
            trailerSectionTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrailerRecyclerView.getVisibility() == View.GONE) {
                        mTrailerRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mTrailerRecyclerView.setVisibility(View.GONE);
                    }
                }
            });

            // Prevent to reload data if bundle is present.
            if (savedInstanceState == null) {
            /* If device is connected to internet and so it's able to fetch movies' reviews and trailers,
             * loaders are initialized to begin the fetching processes.
             */
            if (UtilityActivity.deviceIsConnected(getActivity())) {
                getLoaderManager().initLoader(REVIEW_LOADER_ID, null, mLoaderCallbackReview);
                getLoaderManager().initLoader(TRAILER_LOADER_ID, null, mLoaderCallbackTrailer);
            } else {
            /* If device is not connected to internet, both reviews and trailers containers are made
             * invisible.
             */
                reviewContainerCardView.setVisibility(View.INVISIBLE);
                trailerContainerCardView.setVisibility(View.INVISIBLE);
            }
            reviewArrayList.clear();
            trailerArrayList.clear();
            getLoaderManager().restartLoader(REVIEW_LOADER_ID, null, mLoaderCallbackReview).forceLoad();
            getLoaderManager().restartLoader(TRAILER_LOADER_ID, null, mLoaderCallbackTrailer).forceLoad();
            }
        }

        return rootView;
    }

    /* Set @OnClick method on FAB button to set (or unset) a movie as favorite.
     * If user sets it as favorite, its data are stored in the movie.db and a Snackbar
     * message advices user completing the action. On the contrary, if movie is yet favorite,
     * clicking on FAB button its data are deleted from movie.db.
     */
    @OnClick(R.id.favorite_button)
    void setAsFavorite(){
        if(checkFavoriteMovie(movieId)){
            removeFavoriteMovie(movieId);
            floatingActionButton.setImageResource(R.drawable.ic_star_empty);
//            Snackbar.make(coordinatorLayout, R.string.deleted_favorite_movie, Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
            Toast.makeText(getActivity(), R.string.deleted_favorite_movie, Toast.LENGTH_SHORT ).show();
        }else {
            ContentValues testValues = new ContentValues();
            testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, thumbnail);
            testValues.put(MovieContract.MovieEntry.COLUMN_PLOT, overview);
            testValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, backdrop);
            testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            testValues.put(MovieContract.MovieEntry.COLUMN_RATING, voteRating);
            testValues.put(MovieContract.MovieEntry.COLUMN_DATE, release);
            Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,testValues);
            if(uri!=null){
                floatingActionButton.setImageResource(R.drawable.ic_star_full);
//                Snackbar.make(coordinatorLayout, R.string.added_favorite_movie, Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                Toast.makeText(getActivity(), R.string.added_favorite_movie, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to remove a movie from user's favorites, deleting its data from movie.db
    private void removeFavoriteMovie(long id){
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Method to check if a movie is favorite, querying for its data in movie.db
    private boolean checkFavoriteMovie(int id){
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id, null, null);
        assert cursor != null;
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }



    private LoaderManager.LoaderCallbacks<List<Review>> mLoaderCallbackReview =
            new LoaderManager.LoaderCallbacks<List<Review>>() {

                @Override
                public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
                    Log.e(LOG_TAG, "MovieID is " + movieId);
                    return new ReviewAsyncTaskLoader(getActivity(), movieId);
                }

                @Override
                public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
                    reviewArrayList = data;
                    if (data != null && !data.isEmpty()) {
                        mReviewAdapter = new ReviewAdapter(data);
                        mReviewRecyclerView.setAdapter(mReviewAdapter);
                    } else {
                        // If there is not a list of reviews to populate mReviewRecyclerView,
                        // reviews' container is made invisible.
                        reviewContainerCardView.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onLoaderReset(Loader<List<Review>> loader) {

                }
            };

    private LoaderManager.LoaderCallbacks<List<Trailer>> mLoaderCallbackTrailer =
            new LoaderManager.LoaderCallbacks<List<Trailer>>() {
                @Override
                public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
                    Log.e(LOG_TAG, "MovieID is " + movieId);
                    return new TrailerAsyncTaskLoader(getActivity(), movieId);
                }

                @Override
                public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
                    trailerArrayList = data;
                    if (data != null && !data.isEmpty()) {
                        mTrailerAdapter = new TrailerAdapter(data, getActivity());
                        // Extract first youtube trailer to use for the share intent
                        Trailer firstMovieTrailer = data.get(0);
                        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
                        // Set share action if first youtube trailer for the movie is not null and its key is
                        // available to share the complete youtube video url.
                        if (mShareActionProvider != null) {
                            String shareText = getString(R.string.share_action_text_long,
                                    title, YOUTUBE_WEB_URL + firstMovieTrailer.getmTrailerKey());
                            mShareActionProvider.setShareIntent(createShareIntent(shareText));
                            Log.e(LOG_TAG, "long text to share is: " + shareText);
                        }
                    } else {
                        // If there aren't trailers to display, trailerContainerCardView is invisible.
                        trailerContainerCardView.setVisibility(View.INVISIBLE);
                        // Set a short text message for there's no youtube link to share.
                        if (mShareActionProvider != null) {
                            String shareText = getString(R.string.share_action_text_short, title);
                            mShareActionProvider.setShareIntent(createShareIntent(shareText));
                            Log.e(LOG_TAG, "short text to share is: " + shareText);
                        }

                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Trailer>> loader) {
                //not implemented in this app.

                }
            };
    // Method to open movie's trailer in the Youtube app if available on the device
    // or in a internet browser.
    private void youTubeTrailerVideo(Context context, String trailerKey){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URL + trailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_WEB_URL + trailerKey));

        // Verify that the intent will resolve to an activity, launching Youtube app
        // or an installed internet browser to open youtube page.
        // Substituted catching ActivityNotFoundException with two Runtime Checks
        // to prevent app crash.
        if (appIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(appIntent);
        } else if (webIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(webIntent);
        }
    }

}
