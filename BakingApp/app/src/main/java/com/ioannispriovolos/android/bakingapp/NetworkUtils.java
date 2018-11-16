package com.ioannispriovolos.android.bakingapp;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
public class NetworkUtils {

    public static String getHttpResponse(String url) {
        String responseString = null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }
}

