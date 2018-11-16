package com.example.android.popularmovies1;

/**
 * Created by Motown on 25/03/2018.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MovieParser {
    public static final String TAG = MovieParser.class.getSimpleName();

    public static ArrayList<Movie> parseMovieData(String jsonData, MovieRequestType requestType) throws JSONException {
        ArrayList<Movie> movieData = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData); // https://developer.android.com/reference/org/json/JSONObject.html

        if(requestType == MovieRequestType.POPULAR || requestType == MovieRequestType.TOP_RATED) {

            if(jsonObject.has("results")) {
                JSONArray results = jsonObject.getJSONArray("results");

                for(int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    Movie movie = createMovieObject(result, requestType);
                    movieData.add(movie);
                }
            }
        }

        if(requestType == MovieRequestType.DETAILS) {
            Movie movie = createMovieObject(jsonObject, requestType);
            movieData.add(movie);
        }
        return movieData;
    }

    private static Movie createMovieObject(JSONObject result, MovieRequestType requestType) throws JSONException {
        Movie movie = new Movie();

        movie.title = result.getString("title");
        movie.poster_path = result.getString("poster_path");
        movie.backdrop_path = result.getString("backdrop_path");
        movie.moviedb_id = result.getInt("id");
        movie.popularity = result.getDouble("popularity");
        movie.vote_average = result.getDouble("vote_average");
        movie.vote_count = result.getInt("vote_count");
        movie.overview = result.getString("overview");

        if(requestType == MovieRequestType.DETAILS) {
            movie.runtime = result.getInt("runtime");
            movie.release_date = result.getString("release_date");
        }
        return movie;
    }
}