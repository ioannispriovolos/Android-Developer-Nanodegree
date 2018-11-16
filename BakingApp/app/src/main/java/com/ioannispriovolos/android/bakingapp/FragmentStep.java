package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentStep extends Fragment implements AdapterSteps.StepAdapterOnClickHandler{

    @BindView(R.id.steps_rv_list)
    RecyclerView mRecyclerViewSteps;

    private ArrayList<Step> mStepsList;
    private int mStepNumber;
    public OnStepClickListener mStepCallback;

    public interface OnStepClickListener{

        void onStepItemClicked(ArrayList<Step> stepsModelArrayList, int stepPosition);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            mStepCallback = (OnStepClickListener) context;
        }
        catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " check");
        }
    }

    public FragmentStep(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.steps_list, container, false);
        ButterKnife.bind(this, rootView);
        AdapterSteps adapterSteps = new AdapterSteps(this);
        mRecyclerViewSteps.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        adapterSteps.setStepsDataList(mStepsList);
        mRecyclerViewSteps.setNestedScrollingEnabled(false);
        mRecyclerViewSteps.setAdapter(adapterSteps);
        if (savedInstanceState != null){
            mStepsList = (ArrayList<Step>) savedInstanceState.getSerializable("steps");
            mStepNumber = savedInstanceState.getInt("stepNumber");
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putParcelable("layoutState", mRecyclerViewSteps.getLayoutManager().onSaveInstanceState());
        outState.putSerializable("steps", mStepsList);
        outState.putInt("stepNumber", mStepNumber);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    public void setStepsList(ArrayList<Step> stepsModels){
        mStepsList = stepsModels;
    }

    @Override
    public void onClickStep(ArrayList<Step> stepsModelArrayList, int stepPosition) {

        mStepCallback.onStepItemClicked(stepsModelArrayList, stepPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

