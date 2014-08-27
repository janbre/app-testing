package jasb.no.ntnuemner.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * Created by alex on 8/26/14.
 */
public class CourseDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = CourseDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ntnucourses.db";

    public static final String TABLE_NAME = "courses";
    public static final String _ID = "id";
    public static final String COURSE_CODE = "course_code";
    public static final String COURSE_NORWEGIAN_NAME = "norwegianName";
    public static final String COURSE_ENGLISH_NAME = "englishName";

    public CourseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_COURSE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COURSE_CODE + " TEXT UNIQUE NOT NULL COLLATE NOCASE, " +
                COURSE_NORWEGIAN_NAME + " TEXT NOT NULL, " +
                COURSE_ENGLISH_NAME + " TEXT NOT NULL, " +
                "UNIQUE (" + COURSE_CODE + ") " +
                "ON CONFLICT IGNORE"+" );";

        sqLiteDatabase.execSQL(SQL_CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}
