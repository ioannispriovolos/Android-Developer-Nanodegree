package com.ioannispriovolos.android.mylibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {

    public static String JOKE_PARAM = "joke-param";

    private String jokeFromLib = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        if(getIntent().getStringExtra(LibraryActivity.JOKE_PARAM) != null){
            jokeFromLib = getIntent().getStringExtra(LibraryActivity.JOKE_PARAM);
        }

        getSupportFragmentManager().beginTransaction().add(R.id.library, LibraryFragment.newInstance(jokeFromLib)).commit();
    }
}
