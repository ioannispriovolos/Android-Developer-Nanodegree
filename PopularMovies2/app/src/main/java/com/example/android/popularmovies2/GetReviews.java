package com.example.android.popularmovies2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetReviews extends AsyncTask<Long, Void, List<Review>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = GetReviews.class.getSimpleName();
    private final Listener mListener;

    interface Listener {
        void onReviewsFetchFinished(List<Review> reviews);
    }

    public GetReviews(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Review> doInBackground(Long... params) {

        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();

        MovieService service = retrofit.create(MovieService.class);
        Call<ReviewList> call = service.findReviewsById(movieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<ReviewList> response = call.execute();
            ReviewList reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Database communication error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null) {
            mListener.onReviewsFetchFinished(reviews);
        } else {
            mListener.onReviewsFetchFinished(new ArrayList<Review>());
        }
    }
}
