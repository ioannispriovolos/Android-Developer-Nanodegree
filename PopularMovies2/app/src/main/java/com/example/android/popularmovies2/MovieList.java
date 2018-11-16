package com.example.android.popularmovies2;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieList {

    // https://stackoverflow.com/questions/28957285/what-is-the-basic-purpose-of-serializedname-annotation-in-android-using-gson/28957612
    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public List<Movie> getMovies() {
        return movies;
    }
}
