package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// https://developer.android.com/guide/topics/providers/content-provider-creating
public class TextProvider extends ContentProvider {

    public static final int TEXTS = 100;
    public static final int TEXTS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TextContract.AUTHORITY, TextContract.PATH_TEXTS, TEXTS);
        uriMatcher.addURI(TextContract.AUTHORITY, TextContract.PATH_TEXTS + "/#", TEXTS_WITH_ID);
        return uriMatcher;
    }

    private DbHelper DbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DbHelper = new DbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = DbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch(match){
            case TEXTS:
                cursor =  db.query(TextContract.TextData.TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);
        String type;
        switch (match){
            case TEXTS:
                type = TextContract.TextData.CONTENT_DIR_TYPE;
                break;
            case TEXTS_WITH_ID:
                type = TextContract.TextData.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new SQLException("Unknown URI: " + uri);
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case TEXTS:
                long id = db.insert(TextContract.TextData.TABLE_NAME,null,contentValues);
                if(id>0){
                    returnUri = ContentUris.withAppendedId(TextContract.TextData.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert rows into "+uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int scriptsDeleted;

        switch (match) {
            case TEXTS:
                scriptsDeleted = db.delete(
                        TextContract.TextData.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TextContract.TextData.TABLE_NAME + "'");
                break;

            case TEXTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                scriptsDeleted = db.delete(TextContract.TextData.TABLE_NAME, "_id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (scriptsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return scriptsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,String[] selectionArgs) {

        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int scriptsUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case TEXTS: {
                scriptsUpdated = db.update(TextContract.TextData.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case TEXTS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                scriptsUpdated = db.update(TextContract.TextData.TABLE_NAME, contentValues, TextContract.TextData._ID + " = ?", new String[]{id});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown URI: " + uri);
            }
        }

        if (scriptsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return scriptsUpdated;
    }
}
