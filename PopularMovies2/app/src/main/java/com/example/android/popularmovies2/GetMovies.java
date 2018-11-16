package com.example.android.popularmovies2;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// https://developer.android.com/reference/android/os/AsyncTask
public class GetMovies extends AsyncTask<Void, Void, List<Movie>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = GetMovies.class.getSimpleName();

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";

    // https://developer.android.com/reference/android/support/annotation/StringDef
    @StringDef({MOST_POPULAR, TOP_RATED, FAVORITES})
    public @interface SORT_BY {
    }

    private final NotifyAboutTaskCompletionCommand mCommand;
    private
    @SORT_BY
    String mSortBy = MOST_POPULAR;

    interface Listener {
        void onFetchFinished(PopularMovies popularMovies);
    }

    public static class NotifyAboutTaskCompletionCommand implements PopularMovies {
        private GetMovies.Listener mListener;
        private List<Movie> mMovies;

        public NotifyAboutTaskCompletionCommand(GetMovies.Listener listener) {
            mListener = listener;
        }

        @Override
        public void execute() {
            mListener.onFetchFinished(this);
        }

        public List<Movie> getMovies() {
            return mMovies;
        }
    }

    public GetMovies(@SORT_BY String sortBy, NotifyAboutTaskCompletionCommand command) {
        mCommand = command;
        mSortBy = sortBy;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mCommand.mMovies = movies;
        } else {
            mCommand.mMovies = new ArrayList<>();
        }
        mCommand.execute();
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();

        MovieService service = retrofit.create(MovieService.class);
        Call<MovieList> call = service.discoverMovies(mSortBy,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<MovieList> response = call.execute();
            MovieList movieList = response.body();
            return movieList.getMovies();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Datbase communication error", e);
        }
        return null;
    }
}
