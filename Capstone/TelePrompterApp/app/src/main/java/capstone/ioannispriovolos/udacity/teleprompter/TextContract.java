package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TextContract {

    public static final String AUTHORITY = "capstone.ioannispriovolos.udacity.teleprompter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TEXTS = "texts";


    // https://stackoverflow.com/questions/16435109/basecolumns-interface-of-a-contentprovider-what-to-put-in-it
    public static final class TextData implements BaseColumns {

        public static final String TABLE_NAME = "texts";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEXTS).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
    }
}
