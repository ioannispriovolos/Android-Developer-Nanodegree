package com.example.android.popularmovies2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
// https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
// https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/data/WeatherDbHelper.java
// https://stackoverflow.com/questions/7899720/what-is-the-use-of-basecolumns-in-android
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "movies.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + DatabasePoster.MovieEntry.TABLE_NAME
                + " (" +
                DatabasePoster.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                DatabasePoster.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabasePoster.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
