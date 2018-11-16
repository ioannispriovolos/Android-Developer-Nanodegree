package com.example.android.popularmovies2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        GetMovies.Listener, MainAdapter.Callbacks {

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    private static final int FAVORITE_MOVIES_LOADER = 0;

    private boolean mTwoPane;
    private RetainedFragment mRetainedFragment;
    private MainAdapter mAdapter;
    private String mSortBy = GetMovies.MOST_POPULAR;

    @BindView(R.id.rv_movie_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.tb_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.title_movie_list);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorLight));
        setSupportActionBar(mToolbar);

        String tag = RetainedFragment.class.getName();
        this.mRetainedFragment = (RetainedFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (this.mRetainedFragment == null) {
            this.mRetainedFragment = new RetainedFragment();
            getSupportFragmentManager().beginTransaction().add(this.mRetainedFragment, tag).commit();
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.grid_number_cols)));
        mAdapter = new MainAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);

        // w900
        mTwoPane = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(movies);
                findViewById(R.id.pb_progress).setVisibility(View.GONE);

                if (mSortBy.equals(GetMovies.FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                }
            }
            updateEmptyState();
        } else {
            fetchMovies(mSortBy);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putString(EXTRA_SORT_BY, mSortBy);

        if (!mSortBy.equals(GetMovies.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_activity, menu);

        switch (mSortBy) {
            case GetMovies.MOST_POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;
            case GetMovies.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case GetMovies.FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                if (mSortBy.equals(GetMovies.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = GetMovies.TOP_RATED;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_most_popular:
                if (mSortBy.equals(GetMovies.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = GetMovies.MOST_POPULAR;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                mSortBy = GetMovies.FAVORITES;
                item.setChecked(true);
                fetchMovies(mSortBy);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void open(Movie movie, int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.ARG_MOVIE, movie);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.ARG_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onFetchFinished(PopularMovies popularMovies) {
        if (popularMovies instanceof GetMovies.NotifyAboutTaskCompletionCommand) {
            mAdapter.add(((GetMovies.NotifyAboutTaskCompletionCommand) popularMovies).getMovies());
            updateEmptyState();
            findViewById(R.id.pb_progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.add(cursor);
        updateEmptyState();
        findViewById(R.id.pb_progress).setVisibility(View.GONE);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.pb_progress).setVisibility(View.VISIBLE);
        return new CursorLoader(this, DatabasePoster.MovieEntry.CONTENT_URI, DatabasePoster.MovieEntry.MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private void fetchMovies(String sortBy) {
        if (!sortBy.equals(GetMovies.FAVORITES)) {
            findViewById(R.id.pb_progress).setVisibility(View.VISIBLE);
            GetMovies.NotifyAboutTaskCompletionCommand command = new GetMovies.NotifyAboutTaskCompletionCommand(this.mRetainedFragment);
            new GetMovies(sortBy, command).execute();
        } else {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        }
    }

    private void updateEmptyState() {
        if (mAdapter.getItemCount() == 0) {
            if (mSortBy.equals(GetMovies.FAVORITES)) {
                findViewById(R.id.empty_state_container).setVisibility(View.GONE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.empty_state_container).setVisibility(View.GONE);
            findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
        }
    }


    public static class RetainedFragment extends Fragment implements GetMovies.Listener {

        private boolean mPaused = false;
        private PopularMovies mWaitingCommand = null;

        public RetainedFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mPaused = true;
        }

        @Override
        public void onResume() {
            super.onResume();
            mPaused = false;
            if (mWaitingCommand != null) {
                onFetchFinished(mWaitingCommand);
            }
        }

        @Override
        public void onFetchFinished(PopularMovies popularMovies) {
            if (getActivity() instanceof GetMovies.Listener && !mPaused) {
                GetMovies.Listener listener = (GetMovies.Listener) getActivity();
                listener.onFetchFinished(popularMovies);
                mWaitingCommand = null;
            } else {
                mWaitingCommand = popularMovies;
            }
        }
    }
}