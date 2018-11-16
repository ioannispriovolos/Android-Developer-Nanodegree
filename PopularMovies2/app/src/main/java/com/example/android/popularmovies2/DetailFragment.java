package com.example.android.popularmovies2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//https://developer.android.com/guide/components/fragments
public class DetailFragment extends Fragment implements GetTrailers.Listener,
        TrailerAdapter.Callbacks, GetReviews.Listener, ReviewAdapter.Callbacks {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private Movie mMovie;
    private TrailerAdapter mTrailerListAdapter;
    private ReviewAdapter mReviewListAdapter;
    private ShareActionProvider mShareActionProvider;

    @BindView(R.id.rv_trailer_list)
    RecyclerView mRecyclerViewForTrailers;
    @BindView(R.id.rv_review_list)
    RecyclerView mRecyclerViewForReviews;

    @BindView(R.id.tv_movie_title)
    TextView mMovieTitleView;
    @BindView(R.id.tv_movie_overview)
    TextView mMovieOverviewView;
    @BindView(R.id.tv_movie_release_date)
    TextView mMovieReleaseDateView;
    @BindView(R.id.tv_movie_user_rating)
    TextView mMovieRatingView;
    @BindView(R.id.iv_movie_poster)
    ImageView mMoviePosterView;
    @BindView(R.id.button_mark_as_favorite)
    Button mButtonMarkAsFavorite;
    @BindView(R.id.button_remove_from_favorites)
    Button mButtonRemoveFromFavorites;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        // http://www.feelzdroid.com/2015/08/collapsing-toolbars-android-example.html
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.ct_toolbar_layout);
        if (appBarLayout != null && activity instanceof DetailActivity) {
            appBarLayout.setTitle(mMovie.getTitle());
            appBarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorLight));
            appBarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorLight));
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.iv_movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.with(activity).load(mMovie.getBackdropUrl(getContext())).config(Bitmap.Config.RGB_565).into(movieBackdrop);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        mMovieTitleView.setText(mMovie.getTitle());
        mMovieOverviewView.setText(mMovie.getOverview());
        mMovieReleaseDateView.setText(mMovie.getReleaseDate(getContext()));

        Picasso.with(getContext()).load(mMovie.getPosterUrl(getContext())).config(Bitmap.Config.RGB_565).into(mMoviePosterView);

        UserRating();
        updateFavoriteButtons();

        // Trailers horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewForTrailers.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
        mRecyclerViewForTrailers.setAdapter(mTrailerListAdapter);
        mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

        // Reviews vertical
        mReviewListAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mRecyclerViewForReviews.setAdapter(mReviewListAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
        } else {
            fetchTrailers();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewListAdapter.add(reviews);
        } else {
            fetchReviews();
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {
        mTrailerListAdapter.add(trailers);

        if (mTrailerListAdapter.getItemCount() > 0) {
            Trailer trailer = mTrailerListAdapter.getTrailers().get(0);
            updateShareActionProvider(trailer);
        }
    }

    // https://developer.android.com/reference/android/os/AsyncTask
    @Override
    public void onReviewsFetchFinished(List<Review> reviews) {
        mReviewListAdapter.add(reviews);
    }

    private void fetchTrailers() {
        GetTrailers task = new GetTrailers(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    private void fetchReviews() {
        GetReviews task = new GetReviews(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getPoster());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getUserRating());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    movieValues.put(DatabasePoster.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                            mMovie.getBackdrop());
                    getContext().getContentResolver().insert(
                            DatabasePoster.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(DatabasePoster.MovieEntry.CONTENT_URI,
                            DatabasePoster.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void UserRating() {
        if (mMovie.getUserRating() != null && !mMovie.getUserRating().isEmpty()) {
            String userRatingStr = getResources().getString(R.string.user_rating, mMovie.getUserRating());
            mMovieRatingView.setText(userRatingStr);

        }
        else {
            mMovieRatingView.setVisibility(View.GONE);
        }
    }

    private void updateFavoriteButtons() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    mButtonRemoveFromFavorites.setVisibility(View.VISIBLE);
                    mButtonMarkAsFavorite.setVisibility(View.GONE);
                } else {
                    mButtonMarkAsFavorite.setVisibility(View.VISIBLE);
                    mButtonRemoveFromFavorites.setVisibility(View.GONE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mButtonMarkAsFavorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markAsFavorite();
                    }
                });

        mButtonRemoveFromFavorites.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromFavorites();
                    }
                });
    }

    private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                DatabasePoster.MovieEntry.CONTENT_URI,
                new String[]{DatabasePoster.MovieEntry.COLUMN_MOVIE_ID}, DatabasePoster.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null, null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        }
        else {
            return false;
        }
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + ": " + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }
}