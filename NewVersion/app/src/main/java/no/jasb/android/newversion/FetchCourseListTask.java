package no.jasb.android.newversion;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import no.jasb.android.newversion.data.CourseDbHelper;


/**
 * Created by alex on 10/4/14.
 */

public class FetchCourseListTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final Context mContext;

    public FetchCourseListTask(Context context) {
        this.mContext = context;
    }

    /*
    Might not need this, since only done once, or at least very seldom?
     */
    @Override
    protected Void doInBackground(String... strings) {
        String courseStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String COURSE_BASE_URL = "http://www.ime.ntnu.no/api/course/-";

            URL url = new URL(COURSE_BASE_URL);

            Log.v(LOG_TAG, "Uri is: " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            courseStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            // If the code didn't successfully get the weather data, there's no point in
            // attempting to parse it
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG,
                            "Error closing stream", e);
                }
            }
        }
        try {
            getCourseListDataFromJson(courseStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Void getCourseListDataFromJson(String courseListStr) throws JSONException {

        final String COURSE = "course";
        final String COURSE_NORWEGIAN_NAME = "norwegianName";
        final String COURSE_ENGLISH_NAME = "englishName";
        final String COURSE_CODE = "code";

        JSONArray courseArray = new JSONObject(courseListStr).getJSONArray(COURSE);
        Vector<ContentValues> cvVector = new Vector<ContentValues>(courseArray.length());
        Log.v(LOG_TAG, "Number of courses found: " + courseArray.length());

        for (int i = 0; i < courseArray.length(); i++) {
            JSONObject courseJson = courseArray.getJSONObject(i);
            ContentValues courseValues = new ContentValues();
            courseValues.put(CourseDbHelper.COURSE_CODE, courseJson.getString(COURSE_CODE));
            courseValues.put(CourseDbHelper.COURSE_NORWEGIAN_NAME, courseJson.getString(COURSE_NORWEGIAN_NAME));
            courseValues.put(CourseDbHelper.COURSE_ENGLISH_NAME, courseJson.getString(COURSE_ENGLISH_NAME));

            cvVector.add(courseValues);
        }


        if (cvVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);
            int rowsInserted = bulkDbInsert(cvArray);
            Log.v(LOG_TAG, "Inserted " + rowsInserted + " rows of course " +
                    "data, cvVector is size " + cvVector.size());
        }
        return null;
    }

    private int bulkDbInsert(ContentValues[] courses) {
        int returnCount = 0;

        // If database already exists, we want to delete it when refreshing. Less work...
        File dbFile = mContext.getDatabasePath(CourseDbHelper.DATABASE_NAME);

        if(dbFile.exists()) {
            // Delete database
            if(mContext.deleteDatabase(CourseDbHelper.DATABASE_NAME)) {
                Log.d(LOG_TAG, "Database deleted");
                SQLiteDatabase db = new CourseDbHelper(this.mContext).getWritableDatabase();
                db.beginTransaction();
                try {
                    for (ContentValues course : courses) {
                        long _id = db.insert(CourseDbHelper.TABLE_NAME, null, course);
                        if (_id != -1) {
                            returnCount++;
                        } else {
                            Log.d(LOG_TAG, "Failed to insert " + course.toString());
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
            } else {
                Log.e(LOG_TAG, "Failed to delete database");
            }
        } else {
            // create new database
            SQLiteDatabase db = new CourseDbHelper(this.mContext).getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues course : courses) {
                    long _id = db.insert(CourseDbHelper.TABLE_NAME, null, course);
                    if (_id != -1) {
                        returnCount++;
                    } else {
                        Log.d(LOG_TAG, "Failed to insert " + course.toString());
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
        return returnCount;
    }
}
