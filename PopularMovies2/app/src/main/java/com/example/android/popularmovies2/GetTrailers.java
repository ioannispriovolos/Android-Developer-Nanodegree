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

public class GetTrailers extends AsyncTask<Long, Void, List<Trailer>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = GetTrailers.class.getSimpleName();
    private final Listener mListener;

    interface Listener {
        void onFetchFinished(List<Trailer> trailers);
    }

    public GetTrailers(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();

        MovieService service = retrofit.create(MovieService.class);
        Call<TrailerList> call = service.findTrailersById(movieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<TrailerList> response = call.execute();
            TrailerList trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Database communication error", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<Trailer>());
        }
    }
}