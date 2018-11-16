package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ioannispriovolos.android.mylibrary.LibraryActivity;

public class MainActivityFragment extends Fragment implements AsyncResponse {

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        Button btnTellJoke = root.findViewById(R.id.btnTellJoke);
        btnTellJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellJoke();
            }
        });
        return root;
    }

    public MainActivityFragment() {

    }

    public void tellJoke() {
        progressDialog = new ProgressDialog(getContext(), R.style.Progress_Dialog_Theme);
        progressDialog.setTitle(getContext().getString(R.string.loading));
        progressDialog.setMessage(getContext().getString(R.string.msg_loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        new EndpointsAsyncTask(this).execute("Ioannis");
    }

    @Override
    public void onResult(String msg) {
        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), LibraryActivity.class);
        intent.putExtra(LibraryActivity.JOKE_PARAM, msg);
        startActivity(intent);
    }

}
