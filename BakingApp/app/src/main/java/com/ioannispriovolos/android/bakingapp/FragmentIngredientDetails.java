package com.ioannispriovolos.android.bakingapp;

import android.os.Bundle;
import android.os.Parcelable;
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

public class FragmentIngredientDetails extends Fragment {

    @BindView(R.id.ingredients_rv_list)
    RecyclerView mRecyclerIngredients;
    private ArrayList<Ingredient> mIngredientsList;
    public FragmentIngredientDetails(){

    }

    // https://developer.android.com/reference/android/view/LayoutInflater
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.ingredients_list, container, false);
        ButterKnife.bind(this, rootView);
        AdapterIngredients mIngredientsAdapter = new AdapterIngredients();
        mRecyclerIngredients.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerIngredients.setNestedScrollingEnabled(false);
        if (savedInstanceState != null){
            mIngredientsList = (ArrayList<Ingredient>) savedInstanceState.getSerializable("ingredients");
        }
        mIngredientsAdapter.setIngredientsList(mIngredientsList);
        mRecyclerIngredients.setAdapter(mIngredientsAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("layoutState", mRecyclerIngredients.getLayoutManager().onSaveInstanceState());
        outState.putSerializable("ingredients", mIngredientsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            Parcelable saveRecyclerState = savedInstanceState.getParcelable("layoutState");
            mIngredientsList = (ArrayList<Ingredient>) savedInstanceState.getSerializable("ingredients");
            mRecyclerIngredients.getLayoutManager().onRestoreInstanceState(saveRecyclerState);
        }
    }

    public void setIngredientsList(ArrayList<Ingredient> ingredientsList){
        mIngredientsList = ingredientsList;
    }
}