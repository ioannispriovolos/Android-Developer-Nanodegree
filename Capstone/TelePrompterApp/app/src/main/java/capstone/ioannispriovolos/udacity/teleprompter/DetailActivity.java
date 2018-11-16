package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// http://www.vogella.com/tutorials/AndroidButterknife/article.html
// https://developers.google.com/admob/android/banner
public class DetailActivity extends AppCompatActivity {

    private static String PositionX;
    private static String PositionY;
    @BindView(R.id.detail_script_body)
    TextView tv_script;
    @BindView(R.id.detail_scroll)
    ScrollView sv;
    @BindView(R.id.detail_main_layout)
    CoordinatorLayout detailLayout;
    @BindView(R.id.adView)
    AdView mAdView;
    private static String[] script;
    private static int scrollX;
    private static int scrollY;
    private static String SCRIPTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent startingIntent = getIntent();
        SCRIPTS = getString(R.string.scripts_tag_main_to_detail);
        PositionX = getString(R.string.POSX);
        PositionY = getString(R.string.POSY);
        if(startingIntent.hasExtra(SCRIPTS)){
            script = startingIntent.getStringArrayExtra(SCRIPTS);
        }
        setTitle(script[0].substring(1));
        tv_script.setText(script[1]);
        tv_script.setContentDescription(script[1]);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sv.scrollTo(scrollX, scrollY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @OnClick(R.id.playButton)
    public void play(){

        Intent intent = new Intent(this, TelePromptActivity.class);
        intent.putExtra(SCRIPTS,script[1]);
        startActivity(intent);
    }

    // https://stackoverflow.com/questions/30978457/how-to-show-snackbar-when-activity-starts
    @OnClick(R.id.removeButton)
    public void remove(){

        if(script[0].startsWith("#")){
            Snackbar snackbar = Snackbar.make(detailLayout, getString(R.string.cannot_remove), Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
        else{
            Uri uri = TextContract.TextData.CONTENT_URI;
            uri = uri.buildUpon().appendPath(script[2]).build();
            getContentResolver().delete(uri,null,null);
            onBackPressed();
            Toast.makeText(this, getString(R.string.script_removed_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(PositionX, sv.getScrollX());
        outState.putInt(PositionY, sv.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollX = savedInstanceState.getInt(PositionX);
        scrollY = savedInstanceState.getInt(PositionY);
    }
}
