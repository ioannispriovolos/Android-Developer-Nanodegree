package com.example.android.popularmovies2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {

    // http://square.github.io/retrofit/
    @GET("3/movie/{sort_by}")
    Call<MovieList> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TrailerList> findTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewList> findReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);
}
