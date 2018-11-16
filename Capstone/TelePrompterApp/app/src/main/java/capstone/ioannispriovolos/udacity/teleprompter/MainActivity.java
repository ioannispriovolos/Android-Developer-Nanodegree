package capstone.ioannispriovolos.udacity.teleprompter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

// https://developer.android.com/guide/components/loaders
public class MainActivity extends AppCompatActivity implements MyAdapter.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_CONSTANT = 123;
    private static String SCRIPTS;
    @BindView(R.id.main_recycler_view)
    RecyclerView rv;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    private ArrayList<Text> texts;
    private FirebaseAnalytics mFBanalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFBanalytics = FirebaseAnalytics.getInstance(this);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        SCRIPTS = getString(R.string.scripts_tag_main_to_detail);
        toolbarLayout.setTitle(getString(R.string.app_name));

        MobileAds.initialize(this,getString(R.string.app_Id));
        getSupportLoaderManager().initLoader(LOADER_CONSTANT,null,this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.add_fab)
    public void addScript(){

        Intent intent = new Intent(this, TextEntry.class);
        startActivity(intent);
    }

    @Override
    public void onClickListener(int position) {

        Intent intent = new Intent(this, DetailActivity.class);
        String[] mScript = new String[3];
        mScript[0] = texts.get(position).title;
        mScript[1] = texts.get(position).desc;
        mScript[2] = String.valueOf(texts.get(position).id);

        //Update Widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetTelePrompt.class));
        WidgetTelePrompt.updateWidget(this, appWidgetManager, appWidgetIds, mScript);
        intent.putExtra(SCRIPTS,mScript);
        startActivity(intent);
    }

    private void extractData(Cursor cursor) {

        texts = new ArrayList<>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                String title = cursor.getString(cursor.getColumnIndex(TextContract.TextData.TITLE));
                Long id = cursor.getLong(cursor.getColumnIndex(TextContract.TextData._ID));
                String desc = cursor.getString(cursor.getColumnIndex(TextContract.TextData.DESCRIPTION));
                texts.add(new Text(title,desc,id));
                Timber.d(title);
            }
            while(cursor.moveToNext());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(TextContract.TextData.CONTENT_URI,
                        null,null,null,null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,final Cursor data) {

        Handler handler = new Handler();
        extractData(data);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {}
        }, 3000);
        final MyAdapter adapter = new MyAdapter(texts,this);
        rv.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.grid_cols),StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
