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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ioannispriovolos.android.mylibrary.LibraryActivity;

public class MainActivityFragment extends Fragment implements AsyncResponse {

    // https://developers.google.com/admob/android/interstitial
    InterstitialAd mInterstitialAd;
    private ProgressDialog progressDialog;

    @Override
    public void onResult(String msg) {

        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), LibraryActivity.class);
        intent.putExtra(LibraryActivity.JOKE_PARAM, msg);
        startActivity(intent);

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main2, container, false);

        AdView mAdView = root.findViewById(R.id.adView);
        Button btnTellJoke = root.findViewById(R.id.btnTellJoke);
        btnTellJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellJoke();
            }
        });
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(getActivity(),
                getResources().getString(R.string.ad_mobile_id));

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        return root;
    }

    // https://developer.android.com/reference/android/app/ProgressDialog
    public void tellJoke() {
        progressDialog = new ProgressDialog(getContext(), R.style.Progress_Dialog_Theme);
        progressDialog.setTitle(getContext().getString(R.string.loading));
        progressDialog.setMessage(getContext().getString(R.string.msg_loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        new EndpointsAsyncTask(this).execute("Ioannis");
    }

}
