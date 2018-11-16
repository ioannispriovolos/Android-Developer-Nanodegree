package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TelePromptActivity extends AppCompatActivity {

    private static String TEXTS;
    private static String PositionX;
    private static String PositionY;
    @BindView(R.id.play_script)
    TextView playScript;
    @BindView(R.id.play_scroll)
    ScrollView sv;
    private String script;
    private Handler handler;
    private Runnable play;
    private boolean isPlaying;
    SharedPreferences sharedPreferences;
    private boolean isMirrored;
    long scrollSpeed;
    private int defaultSpeed = 50;
    private static int scrollY;
    private static int scrollX;

    public TelePromptActivity() {

        play = new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0,sv.getScrollY()+1);
                handler.postDelayed(play, defaultSpeed);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        handler = new Handler();
        Intent startingIntent = getIntent();
        TEXTS = getString(R.string.scripts_tag_main_to_detail);
        PositionX = getString(R.string.POSX);
        PositionY = getString(R.string.POSY);
        if(startingIntent.hasExtra(TEXTS)){
            script = startingIntent.getStringExtra(TEXTS);
        }
        playScript.setText(script);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setupSharedPref();


        start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public int getColor(String col){

        int color;
        switch (col){
            case "red":
                color = Color.RED;
                break;
            case "yellow":
                color = Color.YELLOW;
                break;
            case "green":
                color = Color.GREEN;
                break;
            case "white":
                color = Color.WHITE;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "orange":
                color = Color.RED;
                break;
            default:
                color = Color.BLACK;
                break;
        }
        return color;
    }

    private void setupSharedPref() {

        String speed = sharedPreferences.getString(getString(R.string.pref_speed_key),"10");
        scrollSpeed = Integer.valueOf(speed);
        scrollSpeed=10-scrollSpeed;
        scrollSpeed*=4;
        defaultSpeed+=scrollSpeed;
        Timber.e(String.valueOf(defaultSpeed));
        String color = sharedPreferences.getString(getString(R.string.pref_text_color_key), getString(R.string.pref_color_black_value));
        playScript.setTextColor(getColor(color));
        color = sharedPreferences.getString(getString(R.string.pref_bg_color_key), getString(R.string.pref_color_white_value));
        sv.getRootView().setBackgroundColor(getColor(color));
        String size = sharedPreferences.getString(getString(R.string.pref_text_size_key),"20");
        playScript.setTextSize(Integer.valueOf(size));
        isMirrored = sharedPreferences.getBoolean(getString(R.string.pref_mirror_key),false);
        String font = sharedPreferences.getString(getString(R.string.pref_font_key), "Robotto");
        playScript.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/" + font.toUpperCase() + ".ttf"), 1);
        if(isMirrored) {
            playScript.setScaleX(-1);
            playScript.setScaleY(1);
        }
        else {
            playScript.setTranslationX(1);
        }

    }

    private void stop() {

        handler.removeCallbacksAndMessages(null);
        isPlaying = false;
    }

    private void start() {

        handler.post(play);
        isPlaying = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.play_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.play_pause){
            if(isPlaying){
                stop();
                item.setIcon(R.drawable.ic_play_arrow_white_36dp);
            }
            else{
                start();
                item.setIcon(R.drawable.ic_pause_white_36dp);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return super.onSupportNavigateUp();
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
