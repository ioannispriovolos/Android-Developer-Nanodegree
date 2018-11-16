package com.ioannispriovolos.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// https://developer.android.com/reference/android/app/Fragment
public class DetailActivity extends AppCompatActivity implements FragmentIngredient.OnIngredientsClickListener, FragmentStep.OnStepClickListener{

    private RecipeInfo recipeInfo;
    public static ArrayList<Step> mStepsList = new ArrayList<>();
    public static ArrayList<Ingredient> mIngredientsArrayList = new ArrayList<>();
    private Unbinder mUnbinder;
    private boolean mIngredientsClicked = true;
    private boolean mIsTablet;
    public static String mRecipeTitle;
    private int mStepNumber = 0;
    @BindView(R.id.tv_detail_title) TextView mDetailTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);
        mUnbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){

            recipeInfo = (RecipeInfo) bundle.getSerializable("cakeModel");
            mRecipeTitle = recipeInfo.getCakeName();
            mStepsList = recipeInfo.getSteps();
            mIngredientsArrayList = recipeInfo.getIngredients();
        }

        mDetailTitle.setText(mRecipeTitle);
        getSupportActionBar().setTitle(mRecipeTitle);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentIngredient fragmentIngredient = new FragmentIngredient();
        fragmentIngredient.setIngredientModelArrayList(mIngredientsArrayList);

        if (savedInstanceState == null){
            fragmentManager.beginTransaction().add(R.id.ingredients_container_button, fragmentIngredient).commit();
        }
        else{
            fragmentManager.beginTransaction().replace(R.id.ingredients_container_button, fragmentIngredient).commit();
        }

        FragmentStep fragmentStep = new FragmentStep();
        fragmentStep.setStepsList(mStepsList);
        fragmentManager.beginTransaction().add(R.id.steps_list_container, fragmentStep).commit();

        if (mIsTablet){

            FragmentIngredientDetails fragmentIngredientDetails = new FragmentIngredientDetails();
            fragmentIngredientDetails.setIngredientsList(mIngredientsArrayList);

            if (savedInstanceState == null){

                getSupportFragmentManager().beginTransaction().add(R.id.ingredients_detail_container, fragmentIngredientDetails).commit();
            }
            else {

                if (savedInstanceState != null){
                    Bundle restoreBundle = savedInstanceState.getBundle("newBundle");
                    mIngredientsClicked = restoreBundle.getBoolean("ingredientsSelected");
                    mRecipeTitle = restoreBundle.getString("cake");
                }

                if (mIngredientsClicked){
                    getSupportFragmentManager().beginTransaction().replace(R.id.ingredients_detail_container, fragmentIngredientDetails).commit();
                }
                else {

                    FragmentStepDetails fragmentStepDetails = new FragmentStepDetails();

                    if (mStepsList == null || mStepNumber == 0){
                        Bundle bundle1 = savedInstanceState.getBundle("newBundle");
                        mStepsList = (ArrayList<Step>) bundle1.getSerializable("stepsList");
                        mStepNumber =  bundle1.getInt("stepPosition");
                    }
                    fragmentStepDetails.setStepsListAndPosition(mStepsList, mStepNumber);
                    getSupportFragmentManager().beginTransaction().replace(R.id.ingredients_detail_container, fragmentStepDetails).commit();
                }
            }
        }
    }

    @Override
    public void onIngredientsClicked(ArrayList<Ingredient> ingredientModels) {

        if (mIsTablet){

            mIngredientsClicked = true;
            FragmentIngredientDetails fragmentIngredientDetails = new FragmentIngredientDetails();
            fragmentIngredientDetails.setIngredientsList(ingredientModels);
            getSupportFragmentManager().beginTransaction().replace(R.id.ingredients_detail_container, fragmentIngredientDetails).commit();
        }
        else {

            Intent ingredientsIntent = new Intent( this, IngredientActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ingredients", ingredientModels);
            ingredientsIntent.putExtras( bundle);
            startActivity(ingredientsIntent);
        }
    }

    @Override
    public void onStepItemClicked(ArrayList<Step> stepsModelArrayList, int stepPosition) {
        if (mIsTablet){

            mIngredientsClicked = false;
            FragmentStepDetails fragmentStepDetails = new FragmentStepDetails();
            fragmentStepDetails.setStepsListAndPosition(stepsModelArrayList, stepPosition);
            getSupportFragmentManager().beginTransaction().replace(R.id.ingredients_detail_container, fragmentStepDetails).commit();
        }
        else {

            Intent stepIntent = new Intent( this, StepActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stepsList", mStepsList);
            bundle.putInt("stepNumber", stepPosition);
            stepIntent.putExtra(Intent.EXTRA_TEXT, bundle);
            startActivity(stepIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.action_add_widget){

            mRecipeTitle = recipeInfo.getCakeName();
            mIngredientsArrayList = recipeInfo.getIngredients();
            WidgetService.startActionUpdateIngredientsList(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putSerializable("stepsList", mStepsList);
        bundle.putInt("stepPosition", mStepNumber);

        outState.putBoolean("ingredientsSelected", mIngredientsClicked);
        outState.putString("cake", mRecipeTitle);
        outState.putBundle("newBundle",bundle);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mUnbinder.unbind();
    }
}