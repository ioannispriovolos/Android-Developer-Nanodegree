package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterListFragment extends Fragment implements AdapterRecipe.CakeAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String>{

    private final static String TAG = MasterListFragment.class.getSimpleName();
    private OnImageClickListener mCallback;
    private static final String CAKE_LINK__URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String CAKE_URL_EXTRA = "cake";
    private final Type mRecipeListType = new TypeToken<ArrayList<RecipeInfo>>(){}.getType();
    private final static int CAKE_LOADER = 12;
    private ArrayList<RecipeInfo> mListRecipe;
    private AdapterRecipe adapterRecipe;
    private int intOrientation;

    @BindView(R.id.cake_recycle__list) RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mLoadingProgress;

    public interface OnImageClickListener {
        void onImageSelected(RecipeInfo currentCake);
        void showSnackBarError();
    }

    public MasterListFragment(){

    }

    // https://developer.android.com/guide/topics/ui/layout/gridview
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this,rootView);

        adapterRecipe = new AdapterRecipe(getContext(), this);
        intOrientation = checkDeviceOrientation(getContext());
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), intOrientation, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapterRecipe);

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();

        if (savedInstanceState == null){

            Bundle recipeBundle = new Bundle();
            recipeBundle.putString(CAKE_URL_EXTRA, CAKE_LINK__URL);
            Loader<String> recipeLoader = loaderManager.getLoader(CAKE_LOADER);

            if (recipeLoader == null){
                loaderManager.initLoader(CAKE_LOADER, recipeBundle, this);
            }
            else {
                loaderManager.restartLoader(CAKE_LOADER, recipeBundle, this);
            }
        }
        else {

            loaderManager.initLoader(CAKE_LOADER, null, this);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            mCallback = (OnImageClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageClickListener");
        }
    }
    @Override
    public void onClick(RecipeInfo currentCake){

        mCallback.onImageSelected(currentCake);
    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<String>(getContext()) {

            String mJsonStringCake;
            @Override
            protected void onStartLoading() {
                if (mJsonStringCake != null){
                    deliverResult(mJsonStringCake);
                }
                else {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public String loadInBackground() {

                String cakeUrlString = args.getString(CAKE_URL_EXTRA);
                String searchResults = NetworkUtils.getHttpResponse(cakeUrlString);

                return searchResults;
            }


            @Override
            public void deliverResult(String data) {

                mJsonStringCake = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        mLoadingProgress.setVisibility(View.GONE);

        if (data != null){

            mListRecipe = new Gson().fromJson(data, mRecipeListType);
            adapterRecipe.setCakeList(mListRecipe);
            mRecyclerView.setAdapter(adapterRecipe);

        }
        else {

            mCallback.showSnackBarError();
        }
    }

    private int checkDeviceOrientation(Context context) {

        intOrientation = context.getResources().getConfiguration().orientation;
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);

        if(intOrientation == Configuration.ORIENTATION_PORTRAIT){

            if (tabletSize){

                return 2;
            }
            else{

                return 1;
            }
        }
        else{

            return 2;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();

        bundle.putSerializable("cakeModels", mListRecipe);
        outState.putBundle("newBundle",bundle);
    }
}