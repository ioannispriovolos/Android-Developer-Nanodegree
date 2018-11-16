package com.example.android.popularmovies2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// https://developer.android.com/guide/topics/providers/content-provider-creating.html
public class DatabaseProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int MOVIES = 300;
    private DatabaseHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabasePoster.CONTENT_AUTHORITY;
        matcher.addURI(authority, DatabasePoster.PATH_MOVIE, MOVIES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(DatabasePoster.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("URI UFO: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return DatabasePoster.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("URI UFO: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES: {
                long id = db.insert(DatabasePoster.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DatabasePoster.MovieEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Insert row erros " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("URI UFO: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(DatabasePoster.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("URI UFO: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(DatabasePoster.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("URI UFO: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
