package com.example.android.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView; // https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar; // https://developer.android.com/reference/android/widget/ProgressBar.html
    private Menu mMenu;
    private final MovieRequestType theDefaultMovieRequestType = MovieRequestType.POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        configureRecyclerView();
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        fetchMovieData();
    }

    private MovieRequestType getSelectedMovieRequestType() {
        if(mMenu == null) {
            return theDefaultMovieRequestType;
        }

        if(mMenu.findItem(R.id.popular).isChecked()) {
            return MovieRequestType.POPULAR;
        }

        if(mMenu.findItem(R.id.toprated).isChecked()) {
            return MovieRequestType.TOP_RATED;
        }
        return theDefaultMovieRequestType;
    }

    private void configureRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.movies);
        int columns = getResources().getInteger(R.integer.movie_columns); //https://developer.android.com/reference/android/R.integer.html
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns); // https://developer.android.com/reference/android/support/v7/widget/GridLayoutManager.html
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true); // Same size for all posters
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void fetchMovieData() {
        FetchMovieDataTask task = new FetchMovieDataTask(this);
        task.execute(getSelectedMovieRequestType());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if(theDefaultMovieRequestType == MovieRequestType.POPULAR) {
            menu.findItem(R.id.popular).setChecked(true);
        }

        if(theDefaultMovieRequestType == MovieRequestType.TOP_RATED) {
            menu.findItem(R.id.toprated).setChecked(true);
        }
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.popular) {
            if(getSelectedMovieRequestType() != MovieRequestType.POPULAR) {
                Log.i(TAG, "Popular was not already selected, updating data");
                item.setChecked(true);
                fetchMovieData();
            }
            return true;
        }

        if(id == R.id.toprated) {
            if(getSelectedMovieRequestType() != MovieRequestType.TOP_RATED) {
                Log.i(TAG, "Top rated was not already selected, updating data");
                item.setChecked(true);
                fetchMovieData();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickMovie(Movie movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, movie.moviedb_id);

        startActivity(detailIntent);
    }

    public class FetchMovieDataTask extends AsyncTask<MovieRequestType, Void, ArrayList<Movie>> { // https://developer.android.com/reference/android/os/AsyncTask.html
        private Context mContext;

        public FetchMovieDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(MovieRequestType... movieRequestTypes) {
            URL requestURL = null;
            MovieRequestType requestType = movieRequestTypes[0];

            if(requestType == MovieRequestType.POPULAR) {
                requestURL = NetworkUtils.buildURL(mContext, MovieRequestType.POPULAR);
            }

            if(requestType == MovieRequestType.TOP_RATED) {
                requestURL = NetworkUtils.buildURL(mContext, MovieRequestType.TOP_RATED);
            }

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
                Log.d(TAG, "Response: " + response);

                ArrayList<Movie> movies = MovieParser.parseMovieData(response, requestType);

                return movies;

            } catch (IOException|JSONException e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            mMovieAdapter.setMovieData(movies);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
