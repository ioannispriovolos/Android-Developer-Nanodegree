package com.ioannispriovolos.android.mylibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LibraryFragment extends Fragment{

    private String jokeString;
    private TextView jokeTextView;

    public LibraryFragment() {

    }

    public static LibraryFragment newInstance(String jokeParam) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putString(LibraryActivity.JOKE_PARAM, jokeParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString(LibraryActivity.JOKE_PARAM) != null) {
            jokeString = getArguments().getString(LibraryActivity.JOKE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment layout inflater
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        jokeTextView = view.findViewById(R.id.tv_joke);
        jokeTextView.setText(jokeString);

        return view;
    }
}
