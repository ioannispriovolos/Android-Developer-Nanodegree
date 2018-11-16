package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentIngredient extends Fragment {

    @BindView(R.id.ingredients_button)
    TextView mIngredientsButton;
    private OnIngredientsClickListener mIngredientCallback;
    private ArrayList<Ingredient> mIngredientModelArrayList;

    public interface OnIngredientsClickListener{

        void onIngredientsClicked(ArrayList<Ingredient> ingredientModels);
    }

    public FragmentIngredient(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIngredientCallback = (OnIngredientsClickListener) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " check");
        }
    }

    public void setIngredientModelArrayList(ArrayList<Ingredient> ingredientModels){
        mIngredientModelArrayList = ingredientModels;

    }

    @OnClick(R.id.ingredients_container_button)

    void onClick(){
        mIngredientCallback.onIngredientsClicked(mIngredientModelArrayList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}