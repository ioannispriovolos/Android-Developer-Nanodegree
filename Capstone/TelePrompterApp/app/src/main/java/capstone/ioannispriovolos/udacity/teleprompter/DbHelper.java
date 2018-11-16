package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// https://stackoverflow.com/questions/12015731/android-sqlite-example
public class DbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "Texts.db";
    private static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "+ TextContract.TextData.TABLE_NAME + " (" +
                TextContract.TextData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TextContract.TextData.TITLE + " TEXT NOT NULL, " +
                TextContract.TextData.DESCRIPTION + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TextContract.TextData.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
