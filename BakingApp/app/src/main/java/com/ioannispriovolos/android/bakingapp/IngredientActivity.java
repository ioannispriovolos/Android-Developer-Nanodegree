package com.ioannispriovolos.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class IngredientActivity extends AppCompatActivity {

    private static final String TAG = IngredientActivity.class.getSimpleName();

    @Override
    public void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        Intent intentThatStartThisActivity = getIntent();
        Bundle bundle = intentThatStartThisActivity.getExtras();
        ArrayList<Ingredient>  IngredientsList = (ArrayList<Ingredient>) bundle.getSerializable ("ingredients");

        FragmentIngredientDetails fragmentIngredientDetails = new FragmentIngredientDetails();
        fragmentIngredientDetails.setIngredientsList(IngredientsList);

        getSupportFragmentManager().beginTransaction().add(R.id.ingredient_list_container, fragmentIngredientDetails).commit();
    }
}
