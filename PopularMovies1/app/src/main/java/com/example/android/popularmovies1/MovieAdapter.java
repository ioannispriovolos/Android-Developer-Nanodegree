package com.example.android.popularmovies1;

/**
 * Created by Motown on 25/03/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<Movie> mMovieData;
    private MovieOnClickHandler mMovieOnClickHandler;

    public MovieAdapter(MovieOnClickHandler movieOnClickHandler) {
        mMovieOnClickHandler = movieOnClickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext()); // https://developer.android.com/reference/android/view/LayoutInflater.html
        View view =  inflater.inflate(R.layout.items_grid, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        ImageView imageView = holder.mImageView;
        String imageSize = imageView.getResources().getString(R.string.api_image_size);
        URL imageURL = NetworkUtils.buildImageURL(movie.poster_path, imageSize);

        Picasso.with(holder.mImageView.getContext()).load(imageURL.toString()).placeholder(R.drawable.poster_holder).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if(mMovieData == null) {
            return 0;
        }
        return mMovieData.size();
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.poster_frames);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int postion = getAdapterPosition();
            Movie selectedMovie = mMovieData.get(postion);
            mMovieOnClickHandler.onClickMovie(selectedMovie);
        }
    }

    public interface MovieOnClickHandler {
        void onClickMovie(Movie movie);
    }
}