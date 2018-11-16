package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentStepDetails extends Fragment implements View.OnClickListener, ExoPlayer.EventListener{

    private final static String TAG = FragmentStepDetails.class.getSimpleName();

    private Step mStepsModel;
    private ArrayList<Step> mStepsList;

    @BindView(R.id.scrollView_step_long_description)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.step_long_description)
    TextView mLongDescription;

    @BindView(R.id.no_video_holder) TextView mNoVideo;

    @BindView (R.id.iv_step_detail_noVideo) ImageView mNoVideoImage;

    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.bt_recycler_previous)
    Button mPrevious;
    @BindView(R.id.bt_recycler_next)
    Button mNext;

    private SimpleExoPlayer mExoPlayer;
    private Unbinder unbinder;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    long videoPosition;
    private int mStepNumber;
    boolean mNetworkOk;
    private boolean shouldAutoPlay;

    public FragmentStepDetails(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mNetworkOk = checkNetworkConnection();
        if (mNetworkOk){
            if (savedInstanceState == null){

                if (getVideoUrl() != null && mNetworkOk){

                    shouldAutoPlay = true;
                    mNoVideo.setVisibility(View.GONE);
                    mNoVideoImage.setVisibility(View.GONE);
                    videoPosition = 0;
                    initializeMediaPlayer(Uri.parse(getVideoUrl()));
                }
                else {

                    mPlayerView.setVisibility(View.GONE);
                    //mNoVideo.setVisibility(View.VISIBLE);
                    //mNoVideoImage.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(mStepsModel.getThumbnailURL())) {
                        try {
                            Picasso.with(getActivity()).load(Uri.parse(mStepsModel.getThumbnailURL())).into(mNoVideoImage);
                        } catch (Exception ex) {
                            Picasso.with(getActivity()).load(R.drawable.user_placeholder_error).into(mNoVideoImage);
                        }
                    } else {
                        Picasso.with(getActivity()).load(R.drawable.user_placeholder_error).into(mNoVideoImage);
                    }
                    mNoVideoImage.setVisibility(View.VISIBLE);
                }
            }
            if (savedInstanceState != null){

                mStepsModel = (Step) savedInstanceState.getSerializable("step");
                shouldAutoPlay = savedInstanceState.getBoolean("autoPlay");
                if ( getVideoUrl() != null){

                    mNoVideoImage.setVisibility(View.GONE);
                    mNoVideo.setVisibility(View.GONE);

                    if(savedInstanceState.containsKey("videoPosition")){
                        videoPosition = savedInstanceState.getLong("videoPosition");
                    }
                    initializeMediaPlayer(Uri.parse(getVideoUrl()));
                }
                else {
                    // Unavailable video
                    mPlayerView.setVisibility(View.GONE);
                    //mNoVideo.setVisibility(View.VISIBLE);
                    //mNoVideoImage.setVisibility(View.VISIBLE);


                    if (!TextUtils.isEmpty(mStepsModel.getThumbnailURL())) {
                        try {
                            Picasso.with(getActivity()).load(Uri.parse(mStepsModel.getThumbnailURL())).into(mNoVideoImage);
                        } catch (Exception ex) {
                            Picasso.with(getActivity()).load(R.drawable.user_placeholder_error).into(mNoVideoImage);
                        }
                    } else {
                        Picasso.with(getActivity()).load(R.drawable.user_placeholder_error).into(mNoVideoImage);
                    }
                    mNoVideoImage.setVisibility(View.VISIBLE);
                }
            }
            mLongDescription.setText(mStepsModel.getLongDescription());
            initializeMediaSession();

            mPrevious.setOnClickListener(this);
            mNext.setOnClickListener(this);
        }

        else {
            // Unavailable Internet
            mPlayerView.setVisibility(View.GONE);
            mNoVideo.setVisibility(View.VISIBLE);
            mNoVideo.setText(getResources().getString(R.string.internet_connection_error));
            mNoVideoImage.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public boolean checkNetworkConnection(){

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return   networkInfo!= null && networkInfo.isConnected();
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bt_recycler_previous:
                if (mStepNumber == 0){

                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.firstPositionOfSteps), Toast.LENGTH_SHORT).show();
                }
                else {
                    mStepNumber--;
                    replaceFragment();
                }

                break;
            case R.id.bt_recycler_next:

                if (mStepNumber < mStepsList.size() - 1){
                    mStepNumber++;
                    replaceFragment();
                }
                else {

                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.lastPositionOfSteps), Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    private void replaceFragment(){

        boolean  isTablet = getResources().getBoolean(R.bool.isTablet);
        // Tablet
        if (isTablet){

            FragmentStepDetails fragmentStepDetails = new FragmentStepDetails();
            fragmentStepDetails.setStepsListAndPosition(mStepsList, mStepNumber);

            getFragmentManager().beginTransaction().replace(R.id.ingredients_detail_container, fragmentStepDetails).commit();
        }
        // Smartphone
        else {

            Intent stepIntent = new Intent( getContext(), StepActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stepsList", mStepsList);
            bundle.putInt("stepNumber", mStepNumber);
            stepIntent.putExtra(Intent.EXTRA_TEXT, bundle);
            startActivity(stepIntent);
        }
    }

    private String getVideoUrl(){

        if (! mStepsModel.getVideoURL().equals("")) return mStepsModel.getVideoURL();
        else if(! mStepsModel.getThumbnailURL().equals("")) return  mStepsModel.getThumbnailURL();

        return null;
    }

    private void initializeMediaPlayer(Uri mediaUri){

        if (mExoPlayer == null){

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

            String userAgent = Util.getUserAgent(getActivity(), "Backing instruction video");

            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getActivity(), userAgent), new DefaultExtractorsFactory(), null,null);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(shouldAutoPlay);
            mExoPlayer.seekTo(videoPosition);
            mPlayerView.setPlayer(mExoPlayer);
        }
    }

    private void initializeMediaSession(){

        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback{

        @Override
        public void onPlay() {

            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    private void releasePlayer(){

        if (!mStepsModel.getVideoURL().equals("") && mExoPlayer != null){

            videoPosition = mExoPlayer.getCurrentPosition();
            shouldAutoPlay = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState){

        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putLong("videoPosition", videoPosition);
        outState.putSerializable("step", mStepsModel);
        outState.putBoolean("autoPlay", shouldAutoPlay);
    }

    @Override
    public void onPause() {

        super.onPause();
        if (Util.SDK_INT <= 23){
            releasePlayer();
        }
    }

    @Override
    public void onStart() {

        super.onStart();
        if (Util.SDK_INT > 23){
            if (getVideoUrl() != null && mNetworkOk){

                initializeMediaPlayer(Uri.parse(getVideoUrl()));
            }
        }
    }

    @Override
    public void onStop() {

        super.onStop();
        if (Util.SDK_INT > 23){
            releasePlayer();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Util.SDK_INT <= 23 || mExoPlayer == null){
            if (getVideoUrl() != null && mNetworkOk){

                initializeMediaPlayer(Uri.parse(getVideoUrl()));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mMediaSession != null){
            mMediaSession.setActive(false);
        }
    }

    public void setStepsListAndPosition(ArrayList<Step> stepsModels, int stepPosition){
        mStepsList = stepsModels;
        mStepNumber = stepPosition;

        if (stepsModels != null){
            mStepsModel = mStepsList.get(stepPosition);
        }
    }
}