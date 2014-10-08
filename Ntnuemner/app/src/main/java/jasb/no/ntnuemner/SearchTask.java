package jasb.no.ntnuemner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import jasb.no.ntnuemner.data.CourseDbHelper;

/**
 * Created by alex on 9/20/14.
 */
public class SearchTask {

    private final String LOG_TAG = SearchTask.class.getSimpleName();
    private final Context mContext;

    public SearchTask(Context context) {
        this.mContext = context;
    }

    public String[] search(String query) {
        CourseDbHelper dbHelper = new CourseDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> results = new ArrayList<String>();

        // If query is length 3 or less, only search course codes (e.g. if user searches for MA he'll
        // only get the MAxxxx courses, not every course containing the string "ma"
        if (query.length() <=3) {
            Cursor cursor = searchCourseCode(query, db, dbHelper);
            if (cursor == null) {
                Log.v(LOG_TAG, "Cursor is null");
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                Log.v(LOG_TAG, "Cursor could not moveToFirst");
            } else {
                results.add(cursor.getString(0) + " - " + cursor.getString(1));
                Log.v(LOG_TAG, "Result is: " + cursor.getString(1));
                while (cursor.moveToNext()) {
                    results.add(cursor.getString(0) + " - " + cursor.getString(1));
                    Log.v(LOG_TAG, "Result is: " + cursor.getString(1));
                }
            }
        } else {
            Cursor[] cursors = new Cursor[3];
            cursors[0] = searchCourseCode(query, db, dbHelper);
            cursors[1] = searchNorwegianCourseName(query, db, dbHelper);
            cursors[2] = searchEnglishCourseName(query, db, dbHelper);

            for (Cursor cursor : cursors) {
                if (cursor == null) {
                    Log.v(LOG_TAG, "Cursor is null");
                } else if (!cursor.moveToFirst()) {
                    cursor.close();
                    Log.v(LOG_TAG, "Cursor could not moveToFirst");
                } else {
                    results.add(cursor.getString(0) + " - " + cursor.getString(1));
                    Log.v(LOG_TAG, "Result is: " + cursor.getString(1));
                    while (cursor.moveToNext()) {
                        results.add(cursor.getString(0) + " - " + cursor.getString(1));
                        Log.v(LOG_TAG, "Result is: " + cursor.getString(1));
                    }
                }
            }
        }

        db.close();
        String[] searchResults = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            searchResults[i] = results.get(i);
        }
        Arrays.sort(searchResults);
        return searchResults;
    }

    public Cursor searchCourseCode(String query, SQLiteDatabase db, CourseDbHelper dbHelper) {
        Cursor cursor = db.rawQuery("SELECT " + dbHelper.COURSE_CODE + ", " +
                dbHelper.COURSE_ENGLISH_NAME + " FROM " + dbHelper.TABLE_NAME +
                " WHERE " + dbHelper.COURSE_CODE + " LIKE '" + query + "%';", null);
        return cursor;
    }

    public Cursor searchNorwegianCourseName(String query, SQLiteDatabase db, CourseDbHelper dbHelper) {
        Cursor cursor = db.rawQuery("SELECT " + dbHelper.COURSE_CODE + ", " +
                dbHelper.COURSE_ENGLISH_NAME + " FROM " + dbHelper.TABLE_NAME +
                " WHERE " + dbHelper.COURSE_NORWEGIAN_NAME + " LIKE '" + query + "%';", null);
        return cursor;
    }

    public Cursor searchEnglishCourseName(String query, SQLiteDatabase db, CourseDbHelper dbHelper) {
        Cursor cursor = db.rawQuery("SELECT " + dbHelper.COURSE_CODE + ", " +
                dbHelper.COURSE_ENGLISH_NAME + " FROM " + dbHelper.TABLE_NAME +
                " WHERE " + dbHelper.COURSE_ENGLISH_NAME + " LIKE '" + query + "%';", null);
        return cursor;
    }


}
