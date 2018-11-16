package com.example.android.popularmovies1;

/**
 * Created by Motown on 25/03/2018.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils { // https://github.com/udacity/ud851-Sunshine/blob/student/S12.04-Solution-ResourceQualifiers/app/src/main/java/com/example/android/sunshine/utilities/NetworkUtils.java

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_SCHEME = "https";
    private static final String API_AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String API_PATH = "movie";
    private static final String API_LANGUAGE = "en-US";
    private static final String IMAGE_AUTHORITY = "image.tmdb.org";

    private static Uri.Builder buildBaseUri() {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(API_SCHEME).authority(API_AUTHORITY).appendPath(API_VERSION).appendPath(API_PATH);

        return builder;
    }

    public static URL buildURL(Context context, MovieRequestType requestType) {
        return buildURL(context, requestType, 0);
    }

    public static URL buildURL(Context context, MovieRequestType requestType, int id) {

        Uri.Builder builder = buildBaseUri();

        switch(requestType) {
            case POPULAR:
                builder.appendPath("popular");
                break;

            case TOP_RATED:
                builder.appendPath("top_rated");
                break;

            case DETAILS:
                builder.appendPath(String.valueOf(id));
                break;
        }

        Uri uri = builder.appendQueryParameter("language", API_LANGUAGE).appendQueryParameter("api_key", context.getString(R.string.API_KEY)).build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with constructing URL -- " + uri.toString());
        }

        Log.d(TAG, url.toString());

        return url;
    }

    public static URL buildImageURL(String path, String size) {
        path = path.replace("/", "");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(API_SCHEME).authority(IMAGE_AUTHORITY).appendPath("t").appendPath("p").appendPath(size).appendPath(path);
        Uri uri = builder.build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with constructing URL -- " + url.toString());
        }
        Log.d(TAG, url.toString());
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}