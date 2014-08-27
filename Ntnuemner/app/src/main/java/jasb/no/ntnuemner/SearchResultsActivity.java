package jasb.no.ntnuemner;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;

import jasb.no.ntnuemner.data.CourseDbHelper;

/**
 * Created by alex on 8/26/14.
 */
public class SearchResultsActivity extends Activity {

    private final String LOG_TAG = SearchResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Created!");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "Search query is: " + query);
            CourseDbHelper dbHelper = new CourseDbHelper(this);
            search(query);
        }
    }


    public void search(String query) {
        CourseDbHelper dbHelper = new CourseDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME + " WHERE " + dbHelper.COURSE_CODE + " = '" + query + "';", null);

        Cursor cursor = db.rawQuery("SELECT " + dbHelper.COURSE_CODE + ", " + dbHelper.COURSE_ENGLISH_NAME + " FROM " + dbHelper.TABLE_NAME + " WHERE " + dbHelper.COURSE_CODE + " LIKE '" + query + "%';", null);

        if (cursor == null) {
            Log.v(LOG_TAG, "Cursor is null");
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            Log.v(LOG_TAG, "Cursor could not moveToFirst");
        } else {
            Log.v(LOG_TAG, "Result is: " + cursor.getString(1).toString());
            while (cursor.moveToNext()) {
                Log.v(LOG_TAG, "Result is: " + cursor.getString(1).toString());
            }
        }
        Log.v(LOG_TAG, "cursor contains something! " + cursor.getCount());
    }
}
