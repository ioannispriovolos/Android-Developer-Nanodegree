package com.example.android.popularmovies1;

/**
 * Created by Motown on 25/03/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private int mMovieId;
    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mYearTextView;
    private TextView mRunTime;
    private TextView mRating;
    private TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView)findViewById(R.id.movie_title);
        mYearTextView = (TextView)findViewById(R.id.year);
        mRunTime = (TextView)findViewById(R.id.runtime);
        mRating = (TextView)findViewById(R.id.rating);
        mOverview = (TextView)findViewById(R.id.overview);
        mPosterImageView = (ImageView)findViewById(R.id.poster);

        Intent startingIntent = getIntent();

        if(startingIntent != null) {
            if(startingIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = startingIntent.getIntExtra(Intent.EXTRA_TEXT, 0);
            }
        }

        FetchMovieDataTask task = new FetchMovieDataTask(this);
        task.execute();
    }

    public class FetchMovieDataTask extends AsyncTask<Void, Void, Movie> {
        private Context mContext;

        public FetchMovieDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movie doInBackground(Void... voids) {

            URL requestURL = NetworkUtils.buildURL(mContext, MovieRequestType.DETAILS, mMovieId);

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
                Log.d(TAG, "Response: " + response);

                ArrayList<Movie> movies = MovieParser.parseMovieData(response, MovieRequestType.DETAILS);

                Movie movie = null;
                try {
                    movie = movies.get(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, e.toString());
                }

                return movie;

            } catch (IOException |JSONException e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);

            if(movie != null) {
                String imageSize = mContext.getResources().getString(R.string.api_image_size);

                URL imageURL = NetworkUtils.buildImageURL(movie.poster_path, imageSize);

                Picasso.with(mContext).load(imageURL.toString()).placeholder(R.drawable.poster_holder).into(mPosterImageView);

                String rating = String.valueOf(movie.vote_average) + "/10";
                String year = movie.release_date.split("-")[0];
                String runTime = String.valueOf(movie.runtime) + " min";

                mTitleTextView.setText(movie.title);
                mYearTextView.setText(year);
                mRunTime.setText(runTime);
                mRating.setText(rating);
                mOverview.setText(movie.overview);
            }

        }
    }


}
