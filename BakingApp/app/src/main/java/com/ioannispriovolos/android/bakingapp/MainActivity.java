package com.ioannispriovolos.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener, DataProvider.DataLoadedCallback{

    @BindView(R.id.nested_scroll_view_mainActivity) NestedScrollView nestedScrollView;
    private Unbinder unbinder;

    @Nullable
    private Idling mSimpleIdlingResource;

    // https://developer.android.com/reference/android/support/annotation/VisibleForTesting
    @VisibleForTesting
    @Nonnull
    public IdlingResource getIdlingResource(){

        if (mSimpleIdlingResource == null){
            mSimpleIdlingResource = new Idling();
        }
        return mSimpleIdlingResource;
    }

    @Override
    protected void onStart() {

        super.onStart();
        DataProvider.downloadBackingData(this, MainActivity.this, mSimpleIdlingResource);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        if (savedInstanceState == null){

            MasterListFragment masterListFragment = new MasterListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.master_list_container, masterListFragment).commit();
        }

    }
    @Override
    public void onImageSelected(RecipeInfo currentCake){

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cakeModel", currentCake);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showSnackBarError() {
        Snackbar.make(nestedScrollView, getString(R.string.data_loading_error), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry), new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.master_list_container, new MasterListFragment()).commit();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDone() {
        if (mSimpleIdlingResource != null){
            mSimpleIdlingResource.setIdleState(true);
        }
    }
}